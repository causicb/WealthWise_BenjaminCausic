package com.bcausic.wealthwise.ui.mainscreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import coil.load
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.bcausic.wealthwise.WealthWiseApp
import com.bcausic.wealthwise.base.BaseFragment
import com.bcausic.wealthwise.helpers.CAMERA_CODE
import com.bcausic.wealthwise.helpers.CAMERA_PERMISSION_CODE
import com.bcausic.wealthwise.helpers.GALLERY_CODE
import com.bcausic.wealthwise.helpers.STORAGE_PERMISSION_CODE
import com.bcausic.wealthwise.helpers.initPopupMenu
import com.bcausic.wealthwise.helpers.makeToast
import com.bcausic.wealthwise.R
import com.bcausic.wealthwise.databinding.FragmentMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class MainFragment : BaseFragment<FragmentMainBinding>(), PopupMenu.OnMenuItemClickListener {

    private val mainFragmentViewModel: MainFragmentViewModel by viewModel()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainBinding
        get() = FragmentMainBinding::inflate

    override fun setupUi() {
        observeData()
        setOnClickListeners()
    }

    override fun onResume() {
        super.onResume()
        downloadAvatar()
        mainFragmentViewModel.getAllDataForChart()
    }

    private fun downloadAvatar() {
        mainFragmentViewModel.downloadAvatar()
    }

    private fun observeData() {
        mainFragmentViewModel.isUserSignedOut.observe(viewLifecycleOwner) {
            navigateToAuthentication()
        }
        mainFragmentViewModel.userAvatar.observe(viewLifecycleOwner) {
            if(!it.equals(Uri.EMPTY)) {
                binding.sivAvatar.load(it) {
                    placeholder(R.drawable.ic_launcher_background)
                }
            }
            shouldShowProgressDialog(false)
        }
        mainFragmentViewModel.expensesAndRevenues.observe(viewLifecycleOwner) {
            if(it.first != 0.0 || it.second != 0.0) {
                initPieChart()
                setDataToPieChart(it.first.toFloat(), it.second.toFloat())
                shouldShowProgressDialog(false)
            }
        }
    }

    private fun setOnClickListeners() {
        binding.sivMenu.setOnClickListener {
            initPopupMenu(requireContext(), it, R.menu.actions, this)
        }

        binding.sivAvatar.setOnClickListener {
            checkForPermissions()
        }

        binding.mbtnAddRevenueExpense.setOnClickListener {
            navigateToRevenueExpenseFragment()
        }
    }

    private fun initPieChart() {
        with(binding.pieChart) {
            description.text = ""
            setUsePercentValues(true)
            isDrawHoleEnabled = false
            setTouchEnabled(true)
            setDrawEntryLabels(false)
            setExtraOffsets(20f, 0f, 20f, 20f)
            setUsePercentValues(true)
            isRotationEnabled = true
            setDrawEntryLabels(false)
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.isWordWrapEnabled = true
        }
    }

    private fun setDataToPieChart(expenses: Float, revenues: Float) {
        with(binding.pieChart) {
            setUsePercentValues(false)
            val dataEntries = ArrayList<PieEntry>()
            dataEntries.add(PieEntry(expenses, getString(R.string.expenses)))
            dataEntries.add(PieEntry(revenues, getString(R.string.revenues)))

            val colors: ArrayList<Int> = ArrayList()
            colors.add(Color.DKGRAY)
            colors.add(Color.CYAN)

            val dataSet = PieDataSet(dataEntries, "")
            val data = PieData(dataSet)

            // In Percentage
            data.setValueFormatter(DefaultValueFormatter(2))
            dataSet.sliceSpace = 3f
            dataSet.colors = colors
            this.data = data
            data.setValueTextSize(15f)
            setExtraOffsets(5f, 10f, 5f, 5f)
            animateY(1400, Easing.EasingOption.EaseInOutQuad)

            invalidate()
        }
    }

    private fun checkForPermissions() {
        if(ContextCompat.checkSelfPermission(WealthWiseApp.application, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
        if (ContextCompat.checkSelfPermission(WealthWiseApp.application, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
        else {
            initPopupMenu(requireContext(), binding.sivAvatar, R.menu.camera_or_gallery, this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeToast("Storage permission granted!", lengthLong = false)
            }
        }
        if(requestCode == CAMERA_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeToast("Camera permission granted!", lengthLong = false)
                initPopupMenu(requireContext(), binding.sivAvatar, R.menu.camera_or_gallery, this)
            }
        }
    }

    private fun navigateToAuthentication() {
        findNavController().navigate(
            MainFragmentDirections.actionRevenuesAndExpensesFragmentToAuthenticationFragment()
        )
    }

    private fun navigateToRevenueExpenseFragment() {
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToRevenueAndExpenseFragment()
        )
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.sign_out -> mainFragmentViewModel.signOut()
            R.id.camera -> startCamera()
            R.id.previous_entries -> navigateToPreviousEntriesList()
            else -> startGallery()
        }
        return true
    }

    private fun navigateToPreviousEntriesList() {
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToPreviousEntriesListFragment()
        )
    }

    private fun startCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_CODE)
    }

    private fun startGallery() {
        val pickIntent = Intent()
        pickIntent.type = "image/*"
        pickIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(pickIntent, "Select Picture"), GALLERY_CODE)
    }

    private fun shouldShowProgressDialog(shouldShowProgress: Boolean) {
        if(shouldShowProgress) {
            binding.progressDialog.progressBarBg.visibility = View.VISIBLE
        } else {
            binding.progressDialog.progressBarBg.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                val takenImage = data.data
                if (takenImage != null) {
                    mainFragmentViewModel.uploadAvatar(takenImage)
                    binding.sivAvatar.load(takenImage)
                }
            }
        }
        else if(requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                val takenImage = mainFragmentViewModel.getImageUri(requireContext(), data.extras?.get("data") as Bitmap)
                mainFragmentViewModel.uploadAvatar(takenImage)
                binding.sivAvatar.load(takenImage)
            }
        }
    }
}