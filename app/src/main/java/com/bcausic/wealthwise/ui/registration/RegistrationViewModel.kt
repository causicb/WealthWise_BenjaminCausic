package com.bcausic.wealthwise.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcausic.wealthwise.repos.FirebaseAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel(private val firebaseAuthRepository: FirebaseAuthRepository) : ViewModel() {

    private val _didCreateNewAccont: MutableLiveData<Boolean> = MutableLiveData()
    val didCreateNewAccount: LiveData<Boolean>
        get() = _didCreateNewAccont

    fun createNewAccount(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseAuthRepository.createNewUserWithEmailAndPassword(email, password) {
                _didCreateNewAccont.postValue(it)
            }
        }
    }
}