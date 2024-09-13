package com.bcausic.wealthwise.helpers

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.bcausic.wealthwise.WealthWiseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun makeToast(message: String, lengthLong: Boolean = false){
    CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(WealthWiseApp.application, message, if(lengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun initPopupMenu(context: Context, anchor: View, menuRes: Int, listener: PopupMenu.OnMenuItemClickListener) {
    val popup = PopupMenu(context, anchor)
    val inflater = popup.menuInflater
    inflater.inflate(menuRes, popup.menu)
    popup.setOnMenuItemClickListener(listener)
    popup.show()
}