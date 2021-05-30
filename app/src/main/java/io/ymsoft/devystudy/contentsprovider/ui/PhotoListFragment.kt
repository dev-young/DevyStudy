package io.ymsoft.devystudy.contentsprovider.ui

import android.Manifest
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import io.ymsoft.devystudy.R
import io.ymsoft.devystudy.RunWithPermission
import io.ymsoft.devystudy.databinding.ContentsListFragmentBinding
import io.ymsoft.devystudy.showSnackbar
import io.ymsoft.devystudy.showToast
import timber.log.Timber

class PhotoListFragment(private val showAll: Boolean) : Fragment() {

    companion object {
        fun newInstance(showAll: Boolean = false) = PhotoListFragment(showAll)
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
    private val photoListAdapter = PhotoListAdapter({ i, photo ->
        FullscreenActivity.start(photo.uri, requireActivity())
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main2, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_album) {
            if(!loadAllPhoto.isGranted()){

            }
            parentFragmentManager.popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContentsListFragmentBinding.inflate(inflater, container, false)
        binding.photoList.adapter = photoListAdapter
        binding.bucketList.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.photoList.observe(viewLifecycleOwner, { list ->
            binding.photoList.visibility = View.VISIBLE
            photoListAdapter.submitList(list)
        })
        if(showAll) loadAllPhoto.run()

    }

    private val loadAllPhoto by lazy {
        RunWithPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            .setActionWhenGranted {
                showToast("권한 승인됨")
                viewModel.loadAllPhoto()
            }.setActionWhenDenied { run ->
                Timber.i("권한을 허용해주세요!")
                binding.root.showSnackbar(
                    "권한을 허용해주세요!",
                    Snackbar.LENGTH_INDEFINITE, "OK"
                ) {
                    run.requestPermission()
                }
            }.setActionInsteadPopup { run ->
                run.startPermissionIntent()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.e("")
        _binding = null
    }
}