package io.ymsoft.devystudy.contentsprovider.ui

import android.Manifest
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import io.ymsoft.devystudy.*
import io.ymsoft.devystudy.databinding.ContentsListFragmentBinding
import timber.log.Timber

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bucketList.observe(viewLifecycleOwner, bucketListAdapter::submitList)
        loadBucket.run()
    }

    private val loadBucket by lazy {
        RunWithPermission(this,  Manifest.permission.READ_EXTERNAL_STORAGE)
            .setActionWhenGranted {
                showToast("권한 승인됨")
                viewModel.loadBucket()
            }.setActionWhenDenied { run ->
                Timber.i("권한을 허용해주세요.")
                binding.root.showSnackbar(
                    "권한을 허용해주세요.",
                    Snackbar.LENGTH_INDEFINITE, "OK"
                ) {
                    run.requestPermission()
                }
            }.setActionInsteadPopup { run ->
                run.startPermissionIntent()
            }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}