package com.bcausic.wealthwise.ui.authentication

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bcausic.wealthwise.base.BaseFragment
import com.bcausic.wealthwise.R
import com.bcausic.wealthwise.databinding.FragmentAuthenticationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationFragment : BaseFragment<FragmentAuthenticationBinding>() {

    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAuthenticationBinding
        get() = FragmentAuthenticationBinding::inflate

    override fun setupUi() {
        observeData()
        setOnClickListeners()
    }

    override fun onResume() {
        super.onResume()
        authenticationViewModel.getAllEmails()
        checkIsAlreadySignedIn()
    }

    private fun checkIsAlreadySignedIn() {
        authenticationViewModel.isUserAlreadySignedIn()
    }

    private fun observeData() {
        authenticationViewModel.recentEmails.observe(viewLifecycleOwner) {
            initAutoCompleteItems(it)
        }
        authenticationViewModel.isUserSignedIn.observe(viewLifecycleOwner) {
            shouldShowProgressDialog(shouldShowProgress = false)
            if (it) {
                navigateToMainScreen()
            }
        }
    }

    private fun setOnClickListeners() {
        binding.mtvRegisterNow.setOnClickListener {
            navigateToRegistration()
        }

        binding.btnLogin.setOnClickListener {
            shouldShowProgressDialog(shouldShowProgress = true)
            authenticationViewModel.signIn(
                binding.textInputEditTextEmail.text.toString(),
                binding.textInputEditTextPassword.text.toString()
            )
            authenticationViewModel.insertEmail(binding.textInputEditTextEmail.text.toString())
        }

        binding.textInputEditTextEmail.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                binding.textInputEditTextEmail.setText(
                    parent.getItemAtPosition(position).toString()
                )
            }

        binding.sivMenuDeleteEmails.setOnClickListener {
            initAlertDialog()
        }
    }

    private fun initAutoCompleteItems(recentEmails: List<String>) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recentEmails)
        binding.textInputEditTextEmail.setAdapter(adapter)
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(
            AuthenticationFragmentDirections.actionAuthenticationFragmentToRevenuesAndExpensesFragment()
        )
    }

    private fun navigateToRegistration() {
        findNavController().navigate(
            AuthenticationFragmentDirections.actionAuthenticationFragmentToRegistrationFragment()
        )
    }

    private fun shouldShowProgressDialog(shouldShowProgress: Boolean) {
        if (shouldShowProgress) {
            binding.progressDialog.progressBarBg.visibility = View.VISIBLE
        } else {
            binding.progressDialog.progressBarBg.visibility = View.GONE
        }
    }

    private fun simulateLoading() {
        shouldShowProgressDialog(true)
        Handler(Looper.getMainLooper()).postDelayed({
            shouldShowProgressDialog(false)
        }, 500L)
    }

    private fun initAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.alert_dialog_title))
            .setPositiveButton(getString(R.string.ok_label)) { _, _ ->
                authenticationViewModel.deleteAllEmails()
                simulateLoading()
            }
            .setNegativeButton(getString(R.string.cancel_label)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}