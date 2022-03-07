package com.example.users.mainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressbar.visibility =
                if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        viewModel.selectedUser.observe(viewLifecycleOwner) {
            //todo dataBinding
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