package com.bcausic.wealthwise.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcausic.wealthwise.data.sharedprefs.SharedPrefsManager
import com.bcausic.wealthwise.repos.EmailsRepository
import com.bcausic.wealthwise.repos.FirebaseAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val emailsRepository: EmailsRepository
) : ViewModel() {

    private val _isUserSignedIn: MutableLiveData<Boolean> = MutableLiveData()
    val isUserSignedIn: LiveData<Boolean>
        get() = _isUserSignedIn

    private val _recentEmails: MutableLiveData<List<String>> = MutableLiveData()
    val recentEmails: LiveData<List<String>>
        get() = _recentEmails

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            firebaseAuthRepository.authenticateUserWithEmail(email, password) {
                _isUserSignedIn.postValue(it)
            }
        }
    }

    fun isUserAlreadySignedIn() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentFirebaseUser = firebaseAuthRepository.getCurrentUser()

            if(currentFirebaseUser != null) {
                _isUserSignedIn.postValue(true)
                SharedPrefsManager().saveUserId(currentFirebaseUser.uid)
            } else {
                _isUserSignedIn.postValue(false)
            }
        }
    }

    fun insertEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            emailsRepository.insertEmail(email)
        }
    }

    fun getAllEmails() {
        viewModelScope.launch(Dispatchers.IO) {
            _recentEmails.postValue(emailsRepository.getAllEmails())
        }
    }

    fun deleteAllEmails() {
        viewModelScope.launch(Dispatchers.IO) {
            emailsRepository.deleteAll()
        }
    }
}