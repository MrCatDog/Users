package com.example.users.ui

import android.os.Bundle
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

    private var _binding: UsresListFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: UsersListViewModel by viewModelsExt {
        requireContext().appComponent.provideUsersListViewModel()
    }
    private val recyclerAdapter = RecyclerAdapter(viewModel::listItemClicked)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UsresListFragmentBinding.inflate(inflater)

        assembleRecyclerView()

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

        viewModel.navigateToUserDetails.observe(viewLifecycleOwner) {
            findNavController().navigate(UsersListFragmentDirections.actionUsersListFragmentToUserDetailsFragment(it))
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
}