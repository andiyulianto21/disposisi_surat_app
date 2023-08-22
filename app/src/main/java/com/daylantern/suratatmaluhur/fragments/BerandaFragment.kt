package com.daylantern.suratatmaluhur.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.Constants.BASE_URL
import com.daylantern.suratatmaluhur.Constants.PREF_ID_PEGAWAI
import com.daylantern.suratatmaluhur.Constants.formatTanggalLaporan
import com.daylantern.suratatmaluhur.Constants.toastSuccess
import com.daylantern.suratatmaluhur.Constants.toastWarning
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.databinding.BottomSheetLaporanBinding
import com.daylantern.suratatmaluhur.databinding.BottomSheetUbahPasswordBinding
import com.daylantern.suratatmaluhur.databinding.FragmentBerandaBinding
import com.daylantern.suratatmaluhur.viewmodels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class BerandaFragment : Fragment() {
    
    private lateinit var navC: NavController
    private lateinit var binding: FragmentBerandaBinding
    private val viewModel: ProfileViewModel by viewModels()
    
    @Inject
    lateinit var sharedPref: SharedPreferences
    
    @Inject
    lateinit var apiService: ApiService
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBerandaBinding.inflate(layoutInflater)
        return binding.root
    }
    
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navC = Navigation.findNavController(view)
        val idPegawai = sharedPref.getInt(PREF_ID_PEGAWAI, 0)
        viewModel.notifikasiUnread(idPegawai)
        viewModel.fetchPegawaiLoggedIn(idPegawai)
        
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val menu = bottomNavigationView.menu
        val menuDisposisi = menu.findItem(R.id.menu_disposisi)
        val menuSuratKeluar = menu.findItem(R.id.menu_suratKeluar)
        val levelAkses = sharedPref.getString(Constants.PREF_LEVEL_AKSES, null)
        
        menuDisposisi.isVisible = levelAkses == Constants.LEVEL_PIMPINAN
        menuSuratKeluar.isVisible = levelAkses == Constants.LEVEL_KEPALA_BAGIAN
                || levelAkses == Constants.LEVEL_ADMIN
                || levelAkses == Constants.LEVEL_PEGAWAI_BAGIAN
        
        binding.apply {
            tvLabelMaster.isVisible = levelAkses == Constants.LEVEL_ADMIN
            layoutDataInstansi.isVisible = levelAkses == Constants.LEVEL_ADMIN
            layoutDataPegawai.isVisible = levelAkses == Constants.LEVEL_ADMIN
            layoutDataBagian.isVisible = levelAkses == Constants.LEVEL_ADMIN
            layoutDataKategori.isVisible = levelAkses == Constants.LEVEL_ADMIN
            tvLabelLaporan.isVisible = levelAkses == Constants.LEVEL_ADMIN || levelAkses == Constants.LEVEL_PIMPINAN
            layoutLaporanSurat.isVisible = levelAkses == Constants.LEVEL_ADMIN || levelAkses == Constants.LEVEL_PIMPINAN
        }
        observePegawai()
        observeIsNotifikasiUnread()
        observeUbahPasswordMessage()
        observeErrorMessage()
        observeLogoutMessage()
        observeErrorNetworkMessage()
        
        binding.apply {
            layoutPencarian.setOnClickListener {
                navC.navigate(R.id.action_menu_dashboard_to_pencarianFragment)
            }
            btnNotifikasi.setOnClickListener {
                navC.navigate(R.id.action_menu_beranda_to_kotakPesanFragment)
            }
            layoutDataPegawai.setOnClickListener {
                navC.navigate(R.id.pegawaiFragment)
            }
            layoutDataInstansi.setOnClickListener {
                navC.navigate(R.id.action_menu_beranda_to_instansiFragment)
            }
            layoutDataKategori.setOnClickListener { navC.navigate(R.id.kategoriFragment) }
            layoutDataBagian.setOnClickListener { navC.navigate(R.id.bagianFragment) }
//            layoutUbahPassword.setOnClickListener { bottomSheetUbahPassword() }
            layoutLaporanSurat.setOnClickListener { bottomSheetCetakSuratMasuk() }
            layoutLogout.setOnClickListener {
                Constants.alertDialog(requireContext(),
                    "Logout",
                    "Apakah anda yakin ingin logout dari akun ini?",
                    "Batal", { p0, _ ->
                        p0.cancel()
                    }, "Yakin", { p0, _ ->
                        p0.dismiss()
                        FirebaseMessaging.getInstance().token.addOnSuccessListener {
                            viewModel.deleteTokenNotif(idPegawai, it)
                        }
                    })
            }
        }
    }
    
    private fun observeIsNotifikasiUnread() {
        viewModel.isNotifikasiUnread.observe(viewLifecycleOwner) { isUnread ->
            if (isUnread == null) return@observe
            binding.imgUnread.isVisible = isUnread
        }
    }
    
    private fun observeLogoutMessage() {
        viewModel.logoutMessage.observe(viewLifecycleOwner) {
            if (it != null)
                if (it.status == 200) {
                    toastSuccess(requireContext(), "Berhasil logout")
                    sharedPref.edit().clear().apply()
                    navC.navigate(R.id.loginFragment)
                } else toastWarning(requireContext(), it.message)
        }
    }
    
    private fun bottomSheetCetakSuratMasuk() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val laporanBinding =
            BottomSheetLaporanBinding.inflate(LayoutInflater.from(requireContext()))
        val calendarDari = Calendar.getInstance()
        val calendarSampai = Calendar.getInstance()
        bottomSheet.apply {
            setContentView(laporanBinding.root)
            show()
        }
        laporanBinding.apply {
            Constants.textChangedListener(tilPilihSurat)
            optionPilihSurat.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item, mutableListOf("Surat Masuk", "Surat Keluar")
                )
            )
            inputDariTgl.setOnClickListener {
                val dpd = DatePickerDialog(
                    requireContext(),
                    { datePicker, y, mm, dd ->
                        inputDariTgl.setText("$dd/${mm + 1}/$y")
                        calendarDari.set(y, mm, dd)
                        datePicker.updateDate(y, mm, dd)
                    },
                    calendarDari.get(Calendar.YEAR),
                    calendarDari.get(Calendar.MONTH),
                    calendarDari.get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                dpd.show()
            }
            inputSampaiTgl.setOnClickListener {
                val dpd = DatePickerDialog(
                    requireContext(),
                    { datePicker, y, mm, dd ->
                        inputSampaiTgl.setText("$dd/${mm + 1}/$y")
                        calendarSampai.set(y, mm, dd)
                        datePicker.updateDate(y, mm, dd)
                    },
                    calendarSampai.get(Calendar.YEAR),
                    calendarSampai.get(Calendar.MONTH),
                    calendarSampai.get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.minDate = calendarDari.timeInMillis
                dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
                dpd.show()
            }
            btnBatal.setOnClickListener { bottomSheet.cancel() }
            btnBuatLaporan.setOnClickListener {
                val tipeSurat = optionPilihSurat.text?.toString()
                if (tipeSurat.isNullOrEmpty()) {
                    tilPilihSurat.error = "Tipe Surat masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if (inputDariTgl.text?.toString().isNullOrEmpty()
                    || inputSampaiTgl.text?.toString().isNullOrEmpty()) {
                    toastWarning(requireContext(), "Tanggal masih ada yang kosong, silahkan isi")
                    return@setOnClickListener
                }
                //download
                val dari =
                    formatTanggalLaporan(inputDariTgl.text?.toString()!!).replace("-", "_")
                val sampai =
                    formatTanggalLaporan(inputSampaiTgl.text?.toString()!!).replace(
                        "-",
                        "_"
                    )
                val jenisSurat = if(tipeSurat.contains("masuk", true)) "sm" else "sk"
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    downloadFile("${BASE_URL}laporan_$jenisSurat?dari=$dari&sampai=$sampai",
                        dari,
                        sampai,
                        jenisSurat)
                    return@setOnClickListener
                }
                if (hasWriteExternalStoragePermission()) {
                    
                    downloadFile(
                        "${BASE_URL}laporan_$jenisSurat?dari=$dari&sampai=$sampai",
                        dari,
                        sampai,
                        jenisSurat
                    )
                } else {
                    requestPermissions()
                }
                
            }
        }
    }
    
    private fun hasWriteExternalStoragePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestPermissions() {
        val permissionToRequest = mutableListOf<String>()
        if (!hasWriteExternalStoragePermission()) {
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        if (permissionToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionToRequest.toTypedArray(),
                0
            )
        }
    }
    
    private fun openFile(filePath: File) {
        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".fileprovider",
                filePath
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(intent)
        } catch (e: Exception) {
            toastWarning(requireContext(), "Tidak bisa membuka dokumen karena tidak ada aplikasi pembuka PDF")
        }
    }
    
    private fun downloadFile(linkFile: String, dari: String, sampai: String, jenisSurat: String) {
        try {
            val path = "laporan_${jenisSurat}_${System.currentTimeMillis()}.pdf"
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), path)
            if (file.exists()) {
                file.delete()
            }
            val manager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(linkFile)
            val request = DownloadManager.Request(uri)
            
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, path)
            request.setMimeType("application/pdf")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val downloadId = manager.enqueue(request)
            Toast.makeText(
                requireContext(),
                "Sedang mendownload",
                Toast.LENGTH_SHORT
            ).show()
            
            lifecycleScope.launch(Dispatchers.IO) {
                var downloading = true
                while (downloading) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor: Cursor = manager.query(query)
                    cursor.moveToFirst()
                    val status: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS) ?: 0)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Download berhasil",
                                Toast.LENGTH_SHORT
                            ).show()
                            openFile(
                                File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                                    path
                                )
                            )
                        }
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloading = false
                        withContext(Dispatchers.Main) {
                            toastWarning(requireContext(), "Download Gagal")
                        }
                    }
                    cursor.close()
                    delay(1000)
                }
            }
            
        } catch (e: Exception) {
            e.message?.let { toastWarning(requireContext(), it) }
        }
    }
    
    private fun observeUbahPasswordMessage() {
    
    }
    
    private fun observeErrorNetworkMessage() {
        viewModel.errorNetworkMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Terjadi Error")
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("Refresh") { _, _ ->
                        navC.navigate(R.id.menu_beranda)
                    }
                    .show()
            }
        }
    }
    
    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) toastWarning(requireContext(), msg)
        }
    }
    
    private fun bottomSheetUbahPassword() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomSheetUbahPasswordBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.apply {
            setContentView(bottomSheetBinding.root)
            show()
        }
        
        bottomSheetBinding.apply {
            btnBatalUbahPassword.setOnClickListener { bottomSheet.cancel() }
            
            Constants.textChangedListener(tilPasswordBaru)
            Constants.textChangedListener(tilPasswordKonfirmasi)
            Constants.textChangedListener(tilPasswordSekarang)
            
            btnUbahPassword.setOnClickListener {
                val passwordSekarang = inputPasswordSekarang.text.toString().trim()
                val passwordBaru = inputPasswordBaru.text.toString().trim()
                val passwordKonfirmasi = inputPasswordKonfirmasi.text.toString().trim()
                if (passwordSekarang.isEmpty()) {
                    tilPasswordSekarang.error = "Password Saat Ini masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if (passwordBaru.isEmpty()) {
                    tilPasswordBaru.error = "Password Baru masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if (passwordKonfirmasi.isEmpty()) {
                    tilPasswordKonfirmasi.error = "Konfirmasi Password masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if (passwordBaru.length < 6) {
                    tilPasswordBaru.error = "Password minimal 6 karakter"
                    return@setOnClickListener
                }
                if (passwordBaru != passwordKonfirmasi) {
                    tilPasswordBaru.error =
                        "Password dan konfirmasi password tidak sama, silahkan cocokkan"
                    tilPasswordKonfirmasi.error =
                        "Password dan konfirmasi password tidak sama, silahkan cocokkan"
                    return@setOnClickListener
                }
                viewModel.ubahPassword(
                    sharedPref.getInt(PREF_ID_PEGAWAI, 0),
                    passwordSekarang,
                    passwordKonfirmasi
                )
            }
            viewModel.ubahPasswordMessage.observe(viewLifecycleOwner) { result ->
                if (result != null)
                    if (result.status == 200) {
                        toastSuccess(requireContext(), result.message)
                        bottomSheet.dismiss()
                    } else {
                        inputPasswordSekarang.requestFocus()
                        inputPasswordSekarang.setText("")
                        toastWarning(requireContext(), result.message)
                    }
            }
        }
    }
    
    private fun observePegawai() {
        viewModel.pegawaiLoggedIn.observe(viewLifecycleOwner) {pegawai ->
            binding.apply {
                tvNamaPegawai.text = pegawai?.nama
                tvEmail.text = pegawai?.email
                tvLevelAkses.text = "Level Akses : ${pegawai?.levelAkses}"
                sharedPref.edit().putString(Constants.PREF_LEVEL_AKSES, pegawai?.levelAkses).apply()
                tvJabatan.text = pegawai?.jabatan
            }
        }
    }
}