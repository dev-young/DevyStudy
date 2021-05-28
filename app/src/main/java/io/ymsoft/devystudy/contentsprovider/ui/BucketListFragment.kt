package io.ymsoft.devystudy.contentsprovider.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.ymsoft.devystudy.MainActivity
import io.ymsoft.devystudy.R
import io.ymsoft.devystudy.databinding.ContentsListFragmentBinding

class BucketListFragment : Fragment() {

    companion object {
        fun newInstance() = BucketListFragment()
    }

    private var _binding: ContentsListFragmentBinding? =
        null // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(ContentsListViewModel::class.java)
    }
    private val bucketListAdapter = BucketListAdapter({ i, bucket ->
        viewModel.selectBucket(bucket)
        (requireActivity() as MainActivity).addFragment(PhotoListFragment.newInstance())
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContentsListFragmentBinding.inflate(inflater, container, false)

        binding.bucketList.adapter = bucketListAdapter
        binding.photoList.visibility = View.GONE
        return binding.root
    }

    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            viewModel.loadBucket()
        }
        viewModel.bucketList.observe(viewLifecycleOwner, bucketListAdapter::submitList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) viewModel.loadBucket()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}