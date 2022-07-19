package com.example.users.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.users.R
import com.example.users.appComponent
import com.example.users.databinding.UserDetailsFragmentBinding
import com.example.users.utils.RecyclerAdapter
import com.example.users.utils.viewModelsExt
import com.example.users.viewmodels.UserDetailsViewModel
import com.google.android.material.snackbar.Snackbar

class UserDetailsFragment : Fragment() {

    private var _binding: UserDetailsFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val args: UserDetailsFragmentArgs by navArgs()

    private val viewModel: UserDetailsViewModel by viewModelsExt {
        requireContext().appComponent.provideUserDetailsViewModelFactory().create(args.userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserDetailsFragmentBinding.inflate(inflater)

        val recyclerAdapter = RecyclerAdapter(viewModel::listItemClicked)
        val linearLayoutManager = LinearLayoutManager(context)

        binding.friendsList.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
            addItemDecoration(getDividerItemDecoration(linearLayoutManager))
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                userName.text = user.baseUserInfo.name
                userAge.text = user.age.toString()
                userCompany.text = user.company
                userEmail.text = user.baseUserInfo.email
                userFavFruit.text = getString(user.favoriteFruit)
                userPhone.text = user.phone
                userRegistered.text = user.registeredDate
                userAbout.text = user.about
                userLatLon.text =
                    getString(
                        R.string.user_lat_lon_placeholder_with_delimiter,
                        user.location.latitude,
                        user.location.longitude
                    )
                ImageViewCompat.setImageTintList(
                    userEyeColor,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), user.eyeColor))
                )
                binding.userLatLon.setOnClickListener {
                    viewModel.userAddressClicked(user.location)
                }
                recyclerAdapter.setData(user.friends.toList()) //todo: Set -> List не тут должен быть
            }
        }

        viewModel.errorText.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                it,
                Snackbar.LENGTH_LONG
            ).show()
        }

        viewModel.navigateToUserDetails.observe(viewLifecycleOwner) {
            findNavController().navigate(
                UserDetailsFragmentDirections.actionUserDetailsFragmentSelf(
                    it
                )
            )
        }

        viewModel.navigateToMap.observe(viewLifecycleOwner) {
            requireContext().startActivity(Intent(Intent.ACTION_VIEW, it))
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDividerItemDecoration(linearLayoutManager: LinearLayoutManager) =
        DividerItemDecoration(context, linearLayoutManager.orientation).apply {
            setDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.users_divider,
                    null
                )!!
            )
        }
}