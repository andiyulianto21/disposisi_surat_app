package com.daylantern.suratatmaluhur

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputLayout
import com.uk.tastytoasty.TastyToasty
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Constants {
    
    //    const val IP_ADDRESS = "suratatmaluhur.000webhostapp.com"
    //    const val BASE_URL = "https://$IP_ADDRESS/"
    const val IP_ADDRESS = "192.168.1.11"
    const val BASE_URL = "http://$IP_ADDRESS/surat/"
    const val SHARED_PREF_NAME = "arsip_surat"
    const val PREF_ID_PEGAWAI = "idPegawai_login"
    const val PREF_LEVEL_AKSES = "LEVEL_AKSES"
    const val PREF_IS_LOGIN = "IS_LOGIN"
    const val PERMISSION_REQ_CODE = 100
    const val TOKEN_FCM = "token_fcm"
    const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    const val KEY_PHOTO = "key_photo"
    
    const val LEVEL_PIMPINAN = "Pimpinan"
    const val LEVEL_ADMIN = "Admin" //BAU
    const val LEVEL_KEPALA_BAGIAN = "Kepala Bagian"
    const val LEVEL_PEGAWAI_BAGIAN = "Pegawai Bagian"
    const val LEVEL_PENGGUNA = "Pengguna"
    
    const val ERROR_NO_INTERNET =
        "Gagal menemukan koneksi internet, silahkan periksa atau ulangi kembali"
    
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun textChangedListener(til: TextInputLayout) {
        til.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {til.error = null}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }
    
    fun hasWriteExternalStoragePermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun requestPermissions(context: Context, activity: Activity){
        val permissionToRequest = mutableListOf<String>()
        if(!hasWriteExternalStoragePermission(context)){
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        if(permissionToRequest.isNotEmpty()){
            ActivityCompat.requestPermissions(activity, permissionToRequest.toTypedArray(), 0)
        }
    }
    
    fun openFile(filePath: File, context: Context, activity: Activity) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                filePath
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            activity.startActivity(intent)
        }catch (e: Exception){
            toastWarning(context, "Tidak ada aplikasi yang dapat membuka file pdf ini")
        }
    }
    
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
    
    @SuppressLint("SimpleDateFormat")
    fun formatTanggalLaporan(date: String): String {
        val format = SimpleDateFormat("d/m/yyyy")
        val newFormat = SimpleDateFormat("yyyy-mm-dd")
        val afterFormat = format.parse(date)
        return newFormat.format(afterFormat!!)
    }
    
    fun alertDialog(
        context: Context,
        title: String,
        message: String,
        noMessage: String,
        no: (DialogInterface, Int) -> Unit,
        yesMessage: String,
        yes: (DialogInterface, Int) -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(yesMessage, yes)
            .setNeutralButton(noMessage, no)
            .show()
    }
    
    fun toastWarning(context: Context, message: String) {
        return TastyToasty.makeText(
            context,
            message,
            TastyToasty.LONG * 2,
            R.drawable.ic_error_small,
            R.color.red_warning,
            R.color.white,
            false
        ).show()
    }
    
    fun toastNormal(context: Context, message: String) {
        return TastyToasty.makeText(
            context,
            message,
            TastyToasty.LONG * 2,
            R.drawable.baseline_info_24,
            R.color.grey_300,
            R.color.white,
            false
        ).show()
    }
    
    fun toastSuccess(context: Context, message: String) {
        return TastyToasty.makeText(
            context,
            message,
            TastyToasty.LONG * 2,
            R.drawable.ic_success_small,
            R.color.green_success,
            R.color.white,
            false
        ).show()
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
    
    fun convertStringToEpoch(dateString: String, pattern: String): Long {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val date: Date = dateFormat.parse(dateString) ?: return 0
        return date.time
    }
    
    fun convertMonth(monthIndex: Int): String {
        return months[monthIndex]
    }
    
    @SuppressLint("SimpleDateFormat")
    fun convertDateStringToCalendar(date: String, isOnlyDate: Boolean): Calendar {
        val pattern = if (isOnlyDate) {
            "yyyy-MM-dd"
        } else {
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
    
    fun showDate(date: String, isOnlyDate: Boolean, isEnterDate: Boolean = true): String {
        val cal = convertDateStringToCalendar(date, isOnlyDate)
        if (isOnlyDate) {
            return "${cal.get(Calendar.DAY_OF_MONTH)} ${convertMonth(cal.get(Calendar.MONTH))} ${
                cal.get(
                    Calendar.YEAR
                )
            }"
        } else {
            if (isEnterDate) {
                return "${cal.get(Calendar.DAY_OF_MONTH)} ${convertMonth(cal.get(Calendar.MONTH))} ${
                    cal.get(Calendar.YEAR)
                }\n${String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))}:${
                    String.format(
                        "%02d",
                        cal.get(Calendar.MINUTE)
                    )
                }"
            } else {
                return "${cal.get(Calendar.DAY_OF_MONTH)} ${convertMonth(cal.get(Calendar.MONTH))} ${
                    cal.get(Calendar.YEAR)
                } - ${String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))}:${
                    String.format(
                        "%02d",
                        cal.get(Calendar.MINUTE)
                    )
                }"
            }
        }
    }
}