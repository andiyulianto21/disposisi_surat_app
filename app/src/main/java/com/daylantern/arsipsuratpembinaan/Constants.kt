package com.daylantern.arsipsuratpembinaan

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Constants {

    const val IP_ADDRESS = "192.168.1.13"
    const val BASE_URL = "http://$IP_ADDRESS/arsipsuratskripsi/"
    const val SHARED_PREF_NAME = "arsip_surat"
    const val PREF_ID_PEGAWAI = "idPegawai_login"
    const val PREF_JABATAN = "jabatan_login"
    const val PERMISSION_REQ_CODE = 100
    const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    const val KEY_PHOTO = "key_photo"

    fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath?.let { File(it) }
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) ?: 0
            cursor?.moveToFirst()
            cursor?.getString(columnIndex)
        } catch (e: Exception) {
            Log.e("error", "getRealPathFromURI Exception : $e")
            ""
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
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