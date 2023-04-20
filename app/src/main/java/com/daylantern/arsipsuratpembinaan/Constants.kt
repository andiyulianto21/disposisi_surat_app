package com.daylantern.arsipsuratpembinaan

import android.util.Patterns

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
}