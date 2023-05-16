package com.daylantern.arsipsuratpembinaan.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.Constants.BASE_URL
import com.daylantern.arsipsuratpembinaan.Constants.PREF_ID_PEGAWAI
import com.daylantern.arsipsuratpembinaan.Constants.formatTanggalLaporan
import com.daylantern.arsipsuratpembinaan.Constants.toastWarning
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.databinding.BottomSheetLaporanBinding
import com.daylantern.arsipsuratpembinaan.databinding.BottomSheetUbahPasswordBinding
import com.daylantern.arsipsuratpembinaan.databinding.FragmentProfileBinding
import com.daylantern.arsipsuratpembinaan.viewmodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.uk.tastytoasty.TastyToasty
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    
    private lateinit var navC: NavController
    lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    
    @Inject
    lateinit var sharedPref: SharedPreferences
    
    @Inject
    lateinit var apiService: ApiService
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }
    
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        (activity as AppCompatActivity).supportActionBar?.title = "Profil Pengguna"
        navC = Navigation.findNavController(view)
        
        viewModel.fetchPegawaiLoggedIn(sharedPref.getInt(PREF_ID_PEGAWAI, 0))
        observePegawai()
        observeUbahPasswordMessage()
        
        binding.btnUbahDataDiri.setOnClickListener {
            navC.navigate(R.id.action_menu_profil_to_ubahDataDiriFragment)
        }
        
        binding.btnUbahPassword.setOnClickListener {
            navC.navigate(R.id.action_menu_profil_to_gantiPasswordFragment)
        }
        
        binding.layoutDataInstansi.setOnClickListener {
            navC.navigate(R.id.action_menu_profile_to_instansiFragment)
        }
        
        binding.layoutUbahPassword.setOnClickListener {
            bottomSheetUbahPassword()
        }
        
        binding.layoutCetakDisposisi.setOnClickListener {
        }
        
        binding.layoutCetakSuratMasuk.setOnClickListener {
            bottomSheetCetakSuratMasuk()
        }
        
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Logout")
                .setMessage("Apakah anda yakin ingin logout dari akun ini?")
                .setNegativeButton("Batal") { p0, _ ->
                    p0.cancel()
                }.setPositiveButton("Yakin") { _, _ ->
                    viewModel.removeTokenNotif(sharedPref.getInt(PREF_ID_PEGAWAI,0))
                    sharedPref.edit().clear().apply()
                    navC.navigate(R.id.action_menu_profil_to_loginFragment)
                }.show()
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
            tvLabelLaporan.text = "Cetak Laporan Surat Masuk"
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
                if (inputDariTgl.text?.toString().isNullOrEmpty() || inputSampaiTgl.text?.toString().isNullOrEmpty()) {
                    toastWarning(requireContext(), "Tanggal belum lengkap, silahkan isi")
                    return@setOnClickListener
                }
                if (hasWriteExternalStoragePermission()){
                    val dari = formatTanggalLaporan(inputDariTgl.text?.toString()!!).replace("-","_")
                    val sampai = formatTanggalLaporan(inputSampaiTgl.text?.toString()!!).replace("-","_")
                    val filePath = downloadFile("${BASE_URL}android/laporan_sm?dari=$dari&sampai=$sampai")
                    openFile(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), filePath))
                }else {
                    requestPermissions()
                }
            }
        }
    }
    
    private fun hasWriteExternalStoragePermission() = ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
    
    private fun requestPermissions(){
        val permissionToRequest = mutableListOf<String>()
        if(!hasWriteExternalStoragePermission()){
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        if(permissionToRequest.isNotEmpty()){
            ActivityCompat.requestPermissions(requireActivity(), permissionToRequest.toTypedArray(), 0)
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
        }catch (e: Exception){
            toastWarning(requireContext(), "Tidak ada aplikasi yang dapat membuka file pdf ini")
        }
    }
    
    private fun downloadFile(linkFile: String): String {
        var path = ""
        try {
            val manager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(linkFile)
            val request = DownloadManager.Request(uri)
            path = "laporan_sm_${System.currentTimeMillis()}.pdf"
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS,path)
            request.setMimeType("application/pdf")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            manager.enqueue(request)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
        return path
    }
    
    private fun observeUbahPasswordMessage() {
        viewModel.ubahPasswordMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                TastyToasty.makeText(
                    requireContext(),
                    it.messages,
                    TastyToasty.SHORT,
                    if (it.status == 200) R.drawable.ic_done else R.drawable.ic_error,
                    if (it.status == 200) R.color.green_success else R.color.red_warning,
                    R.color.white,
                    false
                ).show()
            }
        }
    }
    
    private fun bottomSheetUbahPassword() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomSheetUbahPasswordBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.apply {
            setContentView(bottomSheetBinding.root)
            setOnCancelListener {
                viewModel.changeVerification(false)
                viewModel.resetPasswordMessage()
            }
            show()
        }
        
        bottomSheetBinding.apply {
            btnBatalUbahPassword.setOnClickListener { bottomSheet.cancel() }
            
            viewModel.isVerified.observe(viewLifecycleOwner) { isVerified ->
                btnUbahPassword.text = if (isVerified) "Ubah Password" else "Verifikasi"
                inputPasswordSekarang.isEnabled = !isVerified
                inputPasswordBaru.isEnabled = isVerified
                inputPasswordKonfirmasi.isEnabled = isVerified
                
                if (isVerified) {
                    inputPasswordBaru.requestFocus()
                    btnUbahPassword.setOnClickListener {
                        if (inputPasswordBaru.text?.toString().isNullOrEmpty()) {
                            toastWarning(
                                requireContext(),
                                "Password Baru masih kosong, silahkan isi"
                            )
                            return@setOnClickListener
                        }
                        if (inputPasswordKonfirmasi.text?.toString().isNullOrEmpty()) {
                            toastWarning(
                                requireContext(),
                                "Konfirmasi Password masih kosong, silahkan isi"
                            )
                            return@setOnClickListener
                        }
                        if (inputPasswordBaru.text?.toString() != inputPasswordKonfirmasi.text?.toString()) {
                            toastWarning(
                                requireContext(),
                                "Password dan konfirmasi password tidak sama, silahkan cocokkan"
                            )
                            return@setOnClickListener
                        }
                        if (inputPasswordKonfirmasi.text?.toString() == inputPasswordSekarang.text?.toString()) {
                            toastWarning(
                                requireContext(),
                                "Password baru dan password lama masih sama, silahkan ubah"
                            )
                            return@setOnClickListener
                        }
                        viewModel.ubahPassword(
                            sharedPref.getInt(PREF_ID_PEGAWAI, 0),
                            inputPasswordKonfirmasi.text?.trim().toString()
                        )
                        bottomSheet.dismiss()
                        viewModel.changeVerification(false)
                        viewModel.resetPasswordMessage()
                    }
                } else {
                    inputPasswordSekarang.requestFocus()
                    btnUbahPassword.setOnClickListener {
                        if (inputPasswordSekarang.text?.toString().isNullOrEmpty()) {
                            toastWarning(
                                requireContext(),
                                "Password Saat Ini masih kosong, silahkan isi"
                            )
                            return@setOnClickListener
                        }
                        viewModel.verifikasiPassword(
                            sharedPref.getInt(PREF_ID_PEGAWAI, 0),
                            inputPasswordSekarang.text?.trim().toString()
                        )
                    }
                }
            }
            
        }
    }
    
    private fun observePegawai() {
        viewModel.pegawaiLoggedIn.observe(viewLifecycleOwner) {
            binding.apply {
                tvNamaPegawai.text = it?.nama
                tvNuptkPegawai.text = it?.nuptk
                tvJabatanPegawai.text = it?.jabatan
            }
        }
    }
}