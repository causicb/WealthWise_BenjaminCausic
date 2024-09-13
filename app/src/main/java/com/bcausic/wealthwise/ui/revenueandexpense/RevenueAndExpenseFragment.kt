package com.bcausic.wealthwise.ui.revenueandexpense

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bcausic.wealthwise.base.BaseFragment
import com.bcausic.wealthwise.helpers.initPopupMenu
import com.bcausic.wealthwise.R
import com.bcausic.wealthwise.databinding.FragmentRevenueAndExpenseBinding
import com.bcausic.wealthwise.helpers.makeToast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class RevenueAndExpenseFragment : BaseFragment<FragmentRevenueAndExpenseBinding>(),
    PopupMenu.OnMenuItemClickListener {

    private val revenueAndExpenseViewModel: RevenueAndExpenseViewModel by viewModel()
    private val receiptText = "Racun iz kafica"

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRevenueAndExpenseBinding
        get() = FragmentRevenueAndExpenseBinding::inflate

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                showCamera()
            } else {
                makeToast("Camera permission needed.")
            }
        }

    private val scanLauncher = registerForActivityResult(ScanContract()) {
        if (it.contents == null) {
            makeToast("Cancelled!")
        } else {
            val receipt = it.contents.lines()
            with(binding) {
                if (receipt.size == 1) {
                    binding.textInputEditTextType.setText(receiptText)
                    binding.textInputEditTextAmount.setText(
                        extractValueFromReceipt(
                            it.contents.substringAfter(
                                "izn="
                            )
                        )
                    )
                } else {
                    textInputEditTextType.setText(receipt[13])
                    textInputEditTextAmount.setText(extractValueFromReceipt(receipt[2]))
                }
                textInputEditTextDescription.setText(getString(R.string.expense))
            }
        }
    }

    private fun extractValueFromReceipt(receiptValue: String) =
        String.format(Locale.getDefault(), "%.2f", receiptValue.toInt() / 100.0)

    override fun setupUi() {
        observeData()
        setOnClickListeners()
    }

    private fun observeData() {
        revenueAndExpenseViewModel.didUploadData.observe(viewLifecycleOwner) {
            shouldShowProgressDialog(false)
            if (it) {
                findNavController().navigateUp()
            }
        }
    }

    private fun setOnClickListeners() {
        binding.mbtnAdd.setOnClickListener {
            revenueAndExpenseViewModel.addRevenueOrExpense(
                type = binding.textInputEditTextType.text.toString(),
                amount = binding.textInputEditTextAmount.text.toString(),
                description = binding.textInputEditTextDescription.text.toString()
            )
            shouldShowProgressDialog(true)
        }

        binding.textInputEditTextDescription.setOnClickListener {
            initPopupMenu(requireContext(), it, R.menu.revenues_expenses, this)
        }

        binding.mbtnScan.setOnClickListener {
            checkCameraPermission(requireContext())
        }

        binding.sivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showCamera() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            setPrompt("Scan QR Code")
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
            setOrientationLocked(false)
        }
        scanLauncher.launch(options)
    }

    private fun checkCameraPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            makeToast("Camera permission required!")
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun shouldShowProgressDialog(shouldShowProgress: Boolean) {
        if (shouldShowProgress) {
            binding.progressDialog.progressBarBg.visibility = View.VISIBLE
        } else {
            binding.progressDialog.progressBarBg.visibility = View.GONE
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.revenue -> binding.textInputEditTextDescription.setText(getString(R.string.revenue))
            else -> binding.textInputEditTextDescription.setText(getString(R.string.expense))
        }
        return true
    }
}