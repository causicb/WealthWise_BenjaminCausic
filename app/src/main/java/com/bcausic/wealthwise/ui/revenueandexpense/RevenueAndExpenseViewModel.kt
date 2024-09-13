package com.bcausic.wealthwise.ui.revenueandexpense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcausic.wealthwise.repos.FirestoreRepository
import kotlinx.coroutines.launch

class RevenueAndExpenseViewModel(private val firestoreRepository: FirestoreRepository) : ViewModel() {

    private val _didUploadData: MutableLiveData<Boolean> = MutableLiveData()
    val didUploadData: LiveData<Boolean>
        get() = _didUploadData

    fun addRevenueOrExpense(type: String, amount: String, description: String) {
        viewModelScope.launch {
            firestoreRepository.addData(type, amount, description) {
                _didUploadData.postValue(it)
            }
        }
    }
}