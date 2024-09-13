package com.bcausic.wealthwise.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<viewBinding: ViewBinding> : Fragment() {

    private lateinit var _binding: viewBinding
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> viewBinding

    val binding: viewBinding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setupUi()
    }

    abstract fun setupUi()
}