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
        val recyclerAdapter = RecyclerAdapter()

        binding.usersList.layoutManager = linearLayoutManager
        binding.usersList.adapter = recyclerAdapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}