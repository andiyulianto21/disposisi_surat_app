package com.daylantern.suratatmaluhur.fragments

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.adapters.RvFileSuratAdapter
import com.daylantern.suratatmaluhur.databinding.BottomSheetReviewKonsepSuratBinding
import com.daylantern.suratatmaluhur.databinding.FragmentDetailSuratKeluarBinding
import com.daylantern.suratatmaluhur.entities.SuratKeluar
import com.daylantern.suratatmaluhur.viewmodels.DetailSuratKeluarViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DetailSuratKeluarFragment : Fragment() {
    
    private lateinit var binding: FragmentDetailSuratKeluarBinding
    private val args: DetailSuratKeluarFragmentArgs by navArgs()
    private val viewModel: DetailSuratKeluarViewModel by viewModels()
    private lateinit var adapterFile: RvFileSuratAdapter
    private lateinit var navC: NavController
    @Inject lateinit var sharedPref: SharedPreferences
    private var levelAkses: String? = null
    private lateinit var bottomSheet: BottomSheetDialog
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailSuratKeluarBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navC = view.findNavController()
    
        bottomSheet = BottomSheetDialog(requireContext())
        levelAkses = sharedPref.getString(Constants.PREF_LEVEL_AKSES, null)
        viewModel.getSuratKeluar(args.idSuratKeluar)
        observeLoading()
        observeResult()
        observeReviewResult()
        binding.apply {
            btnCetakKonsepSurat.setOnClickListener {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    downloadFile(args.idSuratKeluar)
                    return@setOnClickListener
                }
                if(Constants.hasWriteExternalStoragePermission(requireContext())){
//                    viewModel.result.value?.data?.idSurat?.let { it1 -> downloadFile(it1) }
                    downloadFile(args.idSuratKeluar)
                }else {
                    Constants.requestPermissions(requireContext(), requireActivity())
                }
            }
            btnReviewSurat.setOnClickListener {
                showBottomSheetReview()
            }
            
            btnUbah.setOnClickListener {
                val data = viewModel.result.value?.data ?: return@setOnClickListener
                val action = DetailSuratKeluarFragmentDirections.actionDetailSuratKeluarFragmentToEditSuratKeluarFragment(data)
                navC.navigate(action)
            }
        }
    }
    
    private fun observeReviewResult() {
        viewModel.reviewResult.observe(viewLifecycleOwner){result ->
            if(result.status == 200){
                Constants.toastSuccess(requireContext(), result.message)
                bottomSheet.dismiss()
                viewModel.getSuratKeluar(args.idSuratKeluar)
            }else {
                Constants.toastWarning(requireContext(), result.message)
            }
        }
    }
    
    private fun showBottomSheetReview() {
        val bottomSheetBinding = BottomSheetReviewKonsepSuratBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        
        bottomSheetBinding.apply {
            
            optionPenilaianSurat.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdowntext_pegawai, listOf("Dikoreksi", "Diterima")))
            
            btnBatal.setOnClickListener { bottomSheet.dismiss() }
            Constants.textChangedListener(tilInstruksi)
            Constants.textChangedListener(tilPenilaianSurat)
            btnSimpan.setOnClickListener {
                val instruksi = inputInstruksi.text?.trim().toString()
                val penilaian = optionPenilaianSurat.text?.trim().toString()
                if(instruksi.isEmpty()){
                    tilInstruksi.error = "Instruksi masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(penilaian.isEmpty()){
                    tilPenilaianSurat.error = "Penilaian masih kosong, silahkan pilih"
                    return@setOnClickListener
                }
                Constants.alertDialog(requireContext(), "Penilaian Konsep Surat", "Apakah anda yakin ingin menyimpan penilaian terhadap konsep surat ini",
                "Batal", {p0,_ -> p0.cancel()},
                "Simpan", {p0,_ ->
                        viewModel.reviewKonsepSurat(args.idSuratKeluar, instruksi, penilaian)
                        p0.dismiss()
                    })
            }
        }
    }
    
    private fun downloadFile(idSurat: Int) {
        try {
            val path = "konsep_surat_${idSurat}_${System.currentTimeMillis()}.pdf"
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), path)
            if (file.exists()) {
                file.delete()
            }
            val manager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse("${Constants.BASE_URL}cetak_konsep/$idSurat")
            val request = DownloadManager.Request(uri)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS,path)
            request.setMimeType("application/pdf")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val downloadId = manager.enqueue(request)
            
            lifecycleScope.launch(Dispatchers.IO) {
                var downloading = true
                while (downloading) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor: Cursor = manager.query(query)
                    cursor.moveToFirst()
                    val status: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)?:0)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Download berhasil", Toast.LENGTH_SHORT).show()
                            openFile(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), path))
                        }
                    }else if (status == DownloadManager.STATUS_FAILED) {
                        downloading = false
                        withContext(Dispatchers.Main) {
                            Constants.toastWarning(requireContext(), "Download Gagal")
                        }
                    }
                    cursor.close()
                    delay(1000)
                }
            }
            
        } catch (e: Exception) {
            e.message?.let { Constants.toastWarning(requireContext(), it) }
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
            Constants.toastWarning(
                requireContext(),
                "Tidak bisa membuka dokumen karena tidak ada aplikasi pembuka PDF"
            )
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner){isLoading ->
            binding.pbLoading.isVisible = isLoading
            binding.scrollDetailSk.isVisible = !isLoading
        }
    }
    
    private fun observeResult() {
        viewModel.result.observe(viewLifecycleOwner){result ->
            if(result?.status == 200){
                val surat = result.data
                setupRvFile(surat)
                binding.apply {
                    tvLabelLampiran.isVisible = surat.fileSurat.isNotEmpty()
                    btnUbah.isVisible = (levelAkses == "Pegawai Bagian" || levelAkses == "Admin") && surat.statusSurat != "Diterima"
                    btnReviewSurat.isVisible = levelAkses == "Kepala Bagian" && surat.statusSurat != "Diterima"
                    tvNoSurat.text = if(surat.noSurat.isNullOrEmpty()) "-" else surat.noSurat
                    tvPerihal.text = surat.perihal
                    tvKategoriSurat.text = surat.kategoriSurat
                    tvLabelSuratDibuat.text = "Konsep surat keluar dibuat oleh ${surat.namaPenginput}"
                    tvLabelSuratDireview.text = "Review & Penandatangan oleh ${surat.namaPenandatangan}"
                    tvTglSuratKeluarDibuat.text = Constants.showDate(surat.tglSurat, true)
                    tvTglReview.text = surat.tglReview?.let { Constants.showDate(it, false) }
                    tvInstansiTujuan.text = surat.instansiPenerima
                    tvNamaTujuan.text = if(surat.namaPenerima.isNullOrEmpty()) "-" else surat.namaPenerima
                    tvJabatanTujuan.text = if(surat.jabatanPenerima.isNullOrEmpty()) "-" else surat.jabatanPenerima
                    tvTembusan.text = if(surat.tembusan.isEmpty()) "-" else surat.tembusan.joinToString(", ")
                    tvStatusSurat.text = surat.statusSurat
                    tvInstruksiPenandatangan.text = if(surat.instruksiPenandatangan.isNullOrEmpty())  "-" else surat.instruksiPenandatangan
                }
            }
        }
    }
    
    private fun setupRvFile(data: SuratKeluar) {
        adapterFile =
            RvFileSuratAdapter(data.fileSurat.map { it.pathFile.replace("localhost", Constants.IP_ADDRESS) })
        binding.rvFileSurat.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFileSurat.adapter = adapterFile
        adapterFile.setOnItemClicked(object : RvFileSuratAdapter.OnItemClickListener {
            override fun onImageClicked(linkFile: String) {
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_preview_file)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                dialog.show()
                val imgFile = dialog.findViewById<PhotoView>(R.id.img_preview_file)
                Glide.with(requireContext()).load(linkFile).into(imgFile)
                val imgDownload = dialog.findViewById<ImageView>(R.id.img_download_file)
                imgDownload.setOnClickListener {
                    downloadFile(linkFile)
                }
            }
        })
    }
    
    private fun downloadFile(linkFile: String) {
        try {
            val fileName = linkFile.substringAfterLast("/").substringBeforeLast(".")
            val fileExtension = linkFile.substringAfterLast(".", "")
            val path = "$fileName${System.currentTimeMillis()}.$fileExtension"
            val manager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(linkFile)
            val request = DownloadManager.Request(uri)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, path)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val downloadId = manager.enqueue(request)
            
            lifecycleScope.launch(Dispatchers.IO) {
                var downloading = true
                while (downloading) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor: Cursor = manager.query(query)
                    cursor.moveToFirst()
                    val status: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)?:0)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Download berhasil", Toast.LENGTH_SHORT).show()
                            if(fileExtension == "pdf")
                                openFile(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), path))
                        }
                    }else if (status == DownloadManager.STATUS_FAILED) {
                        downloading = false
                        withContext(Dispatchers.Main) {
                            Constants.toastWarning(requireContext(), "Download Gagal")
                        }
                    }
                    cursor.close()
                    delay(1000)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }
}