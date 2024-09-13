package com.bcausic.wealthwise.repos

import com.google.firebase.firestore.FirebaseFirestore
import com.bcausic.wealthwise.data.sharedprefs.SharedPrefsManager
import com.bcausic.wealthwise.helpers.AMOUNT
import com.bcausic.wealthwise.helpers.DAY
import com.bcausic.wealthwise.helpers.DESCRIPTION
import com.bcausic.wealthwise.helpers.HOUR
import com.bcausic.wealthwise.helpers.MINUTE
import com.bcausic.wealthwise.helpers.MONTH
import com.bcausic.wealthwise.helpers.SECOND
import com.bcausic.wealthwise.helpers.TIMESTAMP
import com.bcausic.wealthwise.helpers.TYPE
import com.bcausic.wealthwise.helpers.YEAR
import com.bcausic.wealthwise.helpers.makeToast
import com.bcausic.wealthwise.models.Results
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FirestoreRepository(private val firestore: FirebaseFirestore) {

    private var currentSecond: String = ""
    private var currentMinute: String = ""
    private var currentHour: String = ""
    private var currentDay: String = ""
    private var currentMonth: String = ""
    private var currentYear: String = ""

    suspend fun addData(
        type: String,
        amount: String,
        description: String,
        onResult: (Boolean) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                getCurrentTime()
                val res = Results(
                    Type = type,
                    Amount = amount,
                    Description = description,
                    Year = currentYear,
                    Month = currentMonth,
                    Day = currentDay,
                    Hour = currentHour,
                    Minute = currentMinute,
                    Second = currentSecond,
                    Timestamp = Timestamp.now()
                )
                SharedPrefsManager().getUserId()?.let { it ->
                    firestore.collection(it)
                        .add(res)
                        .addOnCompleteListener {
                            onResult(true)
                        }
                        .addOnFailureListener {
                            onResult(false)
                            makeToast(it.message.toString(), lengthLong = false)
                        }
                }
            } catch (e: Exception) {
                makeToast(e.message.toString(), lengthLong = false)
            }
        }
    }

    private fun getCurrentTime() {
        val cal: Calendar = Calendar.getInstance()

        val sdfSecond = SimpleDateFormat("ss", Locale.ENGLISH)
        val sdfMinute = SimpleDateFormat("mm", Locale.ENGLISH)
        val sdfHour = SimpleDateFormat("hh", Locale.ENGLISH)
        val sdfDay = SimpleDateFormat("dd", Locale.ENGLISH)
        val sdfMonth = SimpleDateFormat("MMMM", Locale.ENGLISH)
        val sdfYear = SimpleDateFormat("yyyy", Locale.ENGLISH)

        currentSecond = sdfSecond.format(cal.time)
        currentMinute = sdfMinute.format(cal.time)
        currentHour = sdfHour.format(cal.time)
        currentDay = sdfDay.format(cal.time)
        currentMonth = sdfMonth.format(cal.time)
        currentYear = sdfYear.format(cal.time)
    }

    suspend fun getAllData(resultData: (List<Results>) -> Unit) {
        withContext(Dispatchers.IO) {
            getCurrentTime()
            try {
                SharedPrefsManager().getUserId()?.let { id ->
                    firestore.collection(id)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener { task ->
                            val listItems = mutableListOf<Results>()
                            task.result.documents.map { item ->
                                listItems.add(
                                    Results(
                                        item[TYPE].toString(),
                                        item[AMOUNT].toString(),
                                        item[DESCRIPTION].toString(),
                                        item[YEAR].toString(),
                                        item[MONTH].toString(),
                                        item[DAY].toString(),
                                        item[HOUR].toString(),
                                        item[MINUTE].toString(),
                                        item[SECOND].toString(),
                                        item[TIMESTAMP] as Timestamp
                                    )
                                )
                            }
                            resultData(listItems)
                        }
                }
            } catch (e: Exception) {
                makeToast(e.message.toString(), lengthLong = false)
            }
        }
    }

    suspend fun getDataForPieChart(resultData: (List<Results>) -> Unit) {
        withContext(Dispatchers.IO) {
            getCurrentTime()
            try {
                SharedPrefsManager().getUserId()?.let { id ->
                    firestore.collection(id)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener { task ->
                            val listItems = mutableListOf<Results>()
                            task.result.documents.map { item ->
                                if (item[MONTH].toString() == currentMonth) {
                                    listItems.add(
                                        Results(
                                            item[TYPE].toString(),
                                            item[AMOUNT].toString(),
                                            item[DESCRIPTION].toString(),
                                            item[YEAR].toString(),
                                            item[MONTH].toString(),
                                            item[DAY].toString(),
                                            item[HOUR].toString(),
                                            item[MINUTE].toString(),
                                            item[SECOND].toString(),
                                            item[TIMESTAMP] as Timestamp
                                        )
                                    )
                                }
                            }
                            resultData(listItems)
                        }
                }
            } catch (e: Exception) {
                makeToast(e.message.toString(), lengthLong = false)
            }
        }
    }
}