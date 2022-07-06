package com.example.users.ui

import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.users.R
import com.example.users.appComponent
import com.example.users.databinding.UsresListFragmentBinding
import com.example.users.viewmodels.UsersListViewModel
import com.example.users.utils.RecyclerAdapter
import com.example.users.utils.viewModelsExt
import com.google.android.material.snackbar.Snackbar

class UsersListFragment : Fragment() {

    companion object {
        const val firstTimeLoadingSharedTag = "firstTimeLoading"
    }

    private var _binding: UsresListFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: UsersListViewModel by viewModelsExt {
        requireContext().appComponent.provideUsersListViewModelFactory().create(
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean(firstTimeLoadingSharedTag, true)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UsresListFragmentBinding.inflate(inflater)
        val recyclerAdapter = RecyclerAdapter(viewModel::listItemClicked)
        val linearLayoutManager = LinearLayoutManager(context)

        binding.usersList.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
            addItemDecoration(getDividerItemDecoration(linearLayoutManager))
        }

        viewModel.users.observe(viewLifecycleOwner) {
            recyclerAdapter.setData(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressbar.visibility =
                if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                it,
                Snackbar.LENGTH_LONG
            ).show()
        }

        viewModel.navigateToUserDetails.observe(viewLifecycleOwner) {
            findNavController().navigate(
                UsersListFragmentDirections.actionUsersListFragmentToUserDetailsFragment(it)
            )
        }

        viewModel.isFirstLoaded.observe(viewLifecycleOwner) {
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                .putBoolean(firstTimeLoadingSharedTag, it).apply()
        }

        binding.refreshBtn.setOnClickListener {
            viewModel.refreshBtnClicked()
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