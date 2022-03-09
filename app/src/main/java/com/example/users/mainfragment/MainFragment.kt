package com.example.users.mainfragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.users.R
import com.example.users.databinding.MainFragmentBinding
import com.example.users.utils.viewModelsExt

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModelsExt {
        MainViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater)
        val linearLayoutManager = LinearLayoutManager(context)
        val recyclerAdapter = RecyclerAdapter(this)

        binding.usersList.layoutManager = linearLayoutManager
        binding.usersList.adapter = recyclerAdapter

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.backBtnPressed()
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
                    this.userName.text = it.baseUserInfo.name
                    this.userAge.text = it.age.toString()
                    this.userCompany.text = it.company
                    this.userEmail.text = it.baseUserInfo.email
                    this.userFavFruit.text = getString(it.favoriteFruit)
                    this.userPhone.text = it.phone
                    this.userRegistered.text = it.registeredDate
                    this.userLatLon.text =
                        getString(R.string.user_lat_lon_placeholder_with_delimiter, it.lat, it.lon)
                    ImageViewCompat.setImageTintList(this.userEyeColor, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), it.eyeColor)))
                }
        }

        viewModel.users.observe(viewLifecycleOwner) {
            recyclerAdapter.setData(it)
        }

        viewModel.errorText.observe(viewLifecycleOwner) {
            //todo кинуть снэк
        }

        binding.refreshBtn.setOnClickListener {
            viewModel.refreshBtnClicked()
        }

        viewModel.isUserVisible.observe(viewLifecycleOwner) {
            binding.userInfo.root.visibility = if (it) {
                callback.isEnabled = it
                View.VISIBLE
            } else {
                callback.isEnabled = it
                View.GONE
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onListItemClicked(item: BaseUserInfo) {
        viewModel.listItemClicked(item)
    }

}