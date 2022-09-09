package com.example.files.files

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.files.R
import com.example.files.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private var fragmentBind: FragmentMainBinding? = null
    private val viewModelFiles: ViewModelFiles by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)
        fragmentBind = binding
        binding.buttonView.setOnClickListener {
            viewModelFiles.loadFile(
                binding.editTextView.text.toString(),
                requireContext()
            )
        }
        observer()
    }

    private fun observer() {
        viewModelFiles.toastDownload.observe(viewLifecycleOwner) { result ->
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
        }
        viewModelFiles.loader.observe(viewLifecycleOwner, ::waitLoader)
    }

    private fun waitLoader(load: Boolean) {
            fragmentBind?.loaderView?.isVisible = load
            fragmentBind?.buttonView?.isEnabled = !load
            fragmentBind?.editTextView?.isEnabled = !load
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBind = null
    }
}