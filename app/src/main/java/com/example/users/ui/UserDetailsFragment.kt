package com.example.users.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.users.R
import com.example.users.appComponent
import com.example.users.databinding.UserDetailsFragmentBinding
import com.example.users.model.domain.FullUserInfo
import com.example.users.utils.RecyclerAdapter
import com.example.users.utils.viewModelsExt
import com.example.users.viewmodels.UserDetailsViewModel
import com.google.android.material.snackbar.Snackbar

class UserDetailsFragment : Fragment() {

    private var _binding: UserDetailsFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: UserDetailsViewModel by viewModelsExt {
        requireContext().appComponent.provideUserDetailsViewModel()
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

        viewModel.friends.observe(viewLifecycleOwner) {
            recyclerAdapter.setData(it)
        }

        viewModel.user.observe(viewLifecycleOwner) {
            binding.apply {
                userName.text = it.baseUserInfo.name
                userAge.text = it.age.toString()
                userCompany.text = it.company
                userEmail.text = it.baseUserInfo.email
                userFavFruit.text = getString(it.favoriteFruit)
                userPhone.text = it.phone
                userRegistered.text = it.registeredDate
                userAbout.text = it.about
                userLatLon.text =
                    getString(
                        R.string.user_lat_lon_placeholder_with_delimiter,
                        it.location.latitude,
                        it.location.longitude
                    )
                ImageViewCompat.setImageTintList(
                    userEyeColor,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), it.eyeColor))
                )
            }
            setLocationListener(it.location)
        }

        viewModel.errorText.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                it ?: getString(R.string.unknown_error_text),
                Snackbar.LENGTH_LONG
            ).show()
        }

        viewModel.navigateToUserDetails.observe(viewLifecycleOwner) {
            findNavController().navigate(UserDetailsFragmentDirections.actionUserDetailsFragmentSelf(it))
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =  null
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

    private fun setLocationListener(location: FullUserInfo.Location) {
        binding.userLatLon.setOnClickListener {
            requireContext().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:${location.latitude},${location.longitude}")
                )
            )
        }
    }
}