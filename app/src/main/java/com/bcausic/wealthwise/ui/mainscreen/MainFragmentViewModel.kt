package com.bcausic.wealthwise.ui.mainscreen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcausic.wealthwise.data.sharedprefs.SharedPrefsManager
import com.bcausic.wealthwise.helpers.EXPENSE
import com.bcausic.wealthwise.models.Results
import com.bcausic.wealthwise.repos.FirebaseCloudStorageRepository
import com.bcausic.wealthwise.repos.FirebaseAuthRepository
import com.bcausic.wealthwise.repos.FirestoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class MainFragmentViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseCloudStorageRepository: FirebaseCloudStorageRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _isUserSignedOut: MutableLiveData<Boolean> = MutableLiveData()
    val isUserSignedOut: LiveData<Boolean>
        get() = _isUserSignedOut

    private val _userAvatar: MutableLiveData<Uri> = MutableLiveData()
    val userAvatar: LiveData<Uri>
        get() = _userAvatar

    private val _expensesAndRevenues: MutableLiveData<Pair<Double, Double>> = MutableLiveData()
    val expensesAndRevenues: LiveData<Pair<Double, Double>>
        get() = _expensesAndRevenues

    private val _didFetchAllData: MutableLiveData<List<Results>> = MutableLiveData()
    val didFetchAllData: LiveData<List<Results>>
        get() = _didFetchAllData

    fun signOut() {
        firebaseAuthRepository.signOut()
        SharedPrefsManager().saveUserId("")
        _isUserSignedOut.postValue(true)
    }

    fun uploadAvatar(photoUri: Uri) {
        viewModelScope.launch {
            firebaseCloudStorageRepository.uploadPhoto(photoUri)
        }
    }

    fun getImageUri(context: Context, image: Bitmap): Uri {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        return Uri.parse(MediaStore.Images.Media.insertImage(context.contentResolver, image, "Title", null))
    }

    fun downloadAvatar() {
        viewModelScope.launch {
            firebaseCloudStorageRepository.downloadPhoto {
                _userAvatar.postValue(it)
            }
        }
    }

    fun getAllDataAsList() {
        viewModelScope.launch {
            firestoreRepository.getAllData {
                _didFetchAllData.postValue(it)
            }
        }
    }

    fun getAllDataForChart() {
        viewModelScope.launch {
            firestoreRepository.getDataForPieChart {
                filterData(it)
            }
        }
    }

    private fun filterData(data: List<Results>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                var expenses = 0.0
                var revenues = 0.0
                data.map {
                    if (it.Description == EXPENSE) {
                        expenses += it.Amount.toDouble()
                    }
                    else {
                        revenues += it.Amount.toDouble()
                    }
                }
                _expensesAndRevenues.postValue(Pair(expenses, revenues))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}