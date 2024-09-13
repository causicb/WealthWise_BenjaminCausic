package com.bcausic.wealthwise.repos

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bcausic.wealthwise.WealthWiseApp
import com.bcausic.wealthwise.data.sharedprefs.SharedPrefsManager
import com.bcausic.wealthwise.helpers.makeToast
import com.bcausic.wealthwise.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseCloudStorageRepository(firebaseCloudStorage: FirebaseStorage) {

    private val storage = firebaseCloudStorage.reference

    suspend fun uploadPhoto(imageUri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                val imageRef = storage.child("${SharedPrefsManager().getUserId()}.jpg")
                imageRef.putFile(imageUri).addOnSuccessListener {
                    makeToast(WealthWiseApp.application.getString(R.string.image_upload_success), lengthLong = false)
                }.await()
            } catch (e: Exception) {
                makeToast(e.message.toString(), lengthLong = false)
            }
        }
    }

    suspend fun downloadPhoto(onResult: (Uri) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val list = mutableListOf<StorageReference>()
                storage.listAll().addOnCompleteListener {
                    list.addAll(it.result.items)
                }.await()

                for(item in list) {
                    if(item.path.contains("${SharedPrefsManager().getUserId()}.jpg")) {
                        storage.child("${SharedPrefsManager().getUserId()}.jpg")
                            .downloadUrl
                            .addOnCompleteListener {
                                onResult(it.result)
                            }
                            .addOnFailureListener {
                                onResult(Uri.EMPTY)
                            }.await()
                    }
                }
            } catch (e: Exception) {
                onResult(Uri.EMPTY)
                makeToast(e.message.toString(), lengthLong = false)
            }
        }
    }
}