package com.daylantern.arsipsuratpembinaan

import android.annotation.SuppressLint
import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

object Constants {

    const val BASE_URL = "http://10.0.2.2/arsipsuratskripsi/"
    const val SHARED_PREF_NAME = "arsip_surat"

    fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val months = listOf(
        "Januari",
        "Februari",
        "Maret",
        "April",
        "Mei",
        "Juni",
        "Juli",
        "Agustus",
        "September",
        "Oktober",
        "November",
        "Desember"
    )

    fun convertMonth(monthIndex: Int): String {
        return months[monthIndex]
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateStringToCalendar(date: String, isOnlyDate: Boolean): Calendar {
        val pattern = if(isOnlyDate){
            "yyyy-MM-dd"
        }else {
            "yyyy-MM-dd HH:mm:ss"
        }
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        val afterFormat = format.parse(date)

        val calendar = Calendar.getInstance()
        if (afterFormat != null) {
            calendar.time = afterFormat
        }
        return calendar
    }

    fun showDate(cal: Calendar, isOnlyDate: Boolean): String {
        if(isOnlyDate){
            return "${cal.get(Calendar.DAY_OF_MONTH)} ${convertMonth(cal.get(Calendar.MONTH))} ${cal.get(Calendar.YEAR)}"
        }else {
            return "${cal.get(Calendar.DAY_OF_MONTH)} ${convertMonth(cal.get(Calendar.MONTH))} ${cal.get(Calendar.YEAR)}" +
                    "\n${String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))}:${String.format("%02d", cal.get(Calendar.MINUTE))}"
        }
    }
}