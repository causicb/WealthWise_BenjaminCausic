package com.bcausic.wealthwise.repos

import com.bcausic.wealthwise.WealthWiseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.bcausic.wealthwise.data.sharedprefs.SharedPrefsManager
import com.bcausic.wealthwise.helpers.makeToast
import com.bcausic.wealthwise.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthRepository(private val firebaseAuth: FirebaseAuth) {

    suspend fun authenticateUserWithEmail(email: String, password: String, onResult: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        onResult(true)
                        it.user?.let { firebaseUser ->
                            SharedPrefsManager().saveUserId(firebaseUser.uid)
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        onResult(false)
                        makeToast(it.message.toString(), lengthLong = false)
                    }.await()
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    suspend fun createNewUserWithEmailAndPassword(email: String, password: String, onResult: (Boolean) -> Unit) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    makeToast(WealthWiseApp.application.getString(R.string.registration_successful), lengthLong = false)
                    onResult(true)
                }
                .addOnFailureListener {
                    makeToast(it.message.toString(), lengthLong = false)
                    onResult(false)
                    it.printStackTrace()
                }.await()
        } catch (e: Exception) {
            onResult(false)
        }
    }

    fun getCurrentUser(): FirebaseUser?  {
        return firebaseAuth.currentUser
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}