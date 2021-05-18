package io.ymsoft.devystudy.tourapi.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.ymsoft.devystudy.databinding.TourFragmentBinding

class TourFragment : Fragment() {

    companion object {
        fun newInstance() = TourFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var _binding: TourFragmentBinding? =
        null // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = TourFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.search.setOnClickListener {
            viewModel.search(binding.word.text.toString())
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.resultText.observe(viewLifecycleOwner, { binding.result.text = it })
        viewModel.isLoading.observe(viewLifecycleOwner, { binding.progress.isVisible = it })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}