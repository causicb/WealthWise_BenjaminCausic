package com.bcausic.wealthwise.ui.registration

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bcausic.wealthwise.base.BaseFragment
import com.bcausic.wealthwise.databinding.FragmentRegistrationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationFragment : BaseFragment<FragmentRegistrationBinding>() {

    private val registrationViewModel: RegistrationViewModel by viewModel()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRegistrationBinding
        get() = FragmentRegistrationBinding::inflate

    override fun setupUi() {
        observeData()
        setOnClickListeners()
    }

    private fun observeData() {
        registrationViewModel.didCreateNewAccount.observe(viewLifecycleOwner) {
            if(it) {
                navigateToAuthentication()
            }
            shouldShowProgressDialog(shouldShowProgress = false)
        }
    }

    private fun setOnClickListeners() {
        binding.sivBack.setOnClickListener {
            navigateToAuthentication()
        }

        binding.btnRegister.setOnClickListener {
            shouldShowProgressDialog(shouldShowProgress = true)
            registrationViewModel.createNewAccount(
                binding.textInputEditTextEmail.text.toString(),
                binding.textInputEditTextPassword.text.toString()
            )
        }
    }

    private fun navigateToAuthentication() {
        findNavController().navigateUp()
    }

    private fun shouldShowProgressDialog(shouldShowProgress: Boolean) {
        if(shouldShowProgress) {
            binding.progressDialog.progressBarBg.visibility = View.VISIBLE
        }
        else {
            binding.progressDialog.progressBarBg.visibility = View.GONE
        }
    }
}