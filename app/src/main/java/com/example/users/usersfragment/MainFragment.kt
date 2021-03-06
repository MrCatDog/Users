package com.example.users.usersfragment

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.users.R
import com.example.users.appComponent
import com.example.users.databinding.MainFragmentBinding
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.domain.FullUserInfo.BaseUserInfo
import com.example.users.utils.viewModelsExt
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: MainViewModel by viewModelsExt {
        requireContext().appComponent.provideMainViewModel()
    }
    private val recyclerAdapter = RecyclerAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater)

        assembleRecyclerView()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.backBtnPressed()
        }

        binding.refreshBtn.setOnClickListener {
            viewModel.refreshBtnClicked()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressbar.visibility =
                if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        viewModel.selectedUser.observe(viewLifecycleOwner) {
            binding.userInfo.apply {
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

        viewModel.users.observe(viewLifecycleOwner) {
            recyclerAdapter.setData(it)
        }

        viewModel.errorText.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                it ?: getString(R.string.unknown_error_text),
                Snackbar.LENGTH_LONG
            ).show()
        }

        viewModel.isUserVisible.observe(viewLifecycleOwner) {
            binding.userInfo.root.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
            callback.isEnabled = it //todo ?????????????????? ?????? ?????? ?????????????? ??????????????????
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun assembleRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        binding.usersList.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
            addItemDecoration(getDividerItemDecoration(linearLayoutManager))
        }
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

    fun onListItemClicked(item: BaseUserInfo) {
        viewModel.listItemClicked(item)
    }

    private fun setLocationListener(location: FullUserInfo.Location) {
        binding.userInfo.userLatLon.setOnClickListener {
            requireContext().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:${location.latitude},${location.longitude}")
                )
            )
        }
    }
}