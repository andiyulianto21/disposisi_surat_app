package com.daylantern.suratatmaluhur.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.daylantern.suratatmaluhur.databinding.FragmentDetailSuratMasukBinding
import com.daylantern.suratatmaluhur.entities.SuratMasuk
import com.daylantern.suratatmaluhur.models.enums.StatusSuratMasuk
import com.daylantern.suratatmaluhur.viewmodels.DetailSuratMasukViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DetailSuratMasukFragment : Fragment() {
    
    private lateinit var binding: FragmentDetailSuratMasukBinding
    private val viewModel: DetailSuratMasukViewModel by viewModels()
    private val args: TambahDisposisiFragmentArgs by navArgs()
    private lateinit var adapterFile: RvFileSuratAdapter
    private lateinit var navC: NavController
    @Inject lateinit var sharedPref: SharedPreferences
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailSuratMasukBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val levelAkses = sharedPref.getString(Constants.PREF_LEVEL_AKSES, "")
        navC = view.findNavController()
        viewModel.fetchSuratMasuk(args.idSuratMasuk)
        observeLoading()
        observeErrorMessage()
        observeSuratMasuk()
        binding.apply {
            btnUbah.setOnClickListener {  }
            btnHapus.setOnClickListener {
                Constants.alertDialog(requireContext(), "Hapus Surat Masuk",
                    "Apakah anda yakin ingin menghapus data surat masuk beserta file dan disposisi-nya?",
                "Batal", {p0,_ -> p0.cancel()},
                    "Hapus", {p0,_ ->
                        p0.dismiss()
                    })
            }
            btnCetakDisposisi.setOnClickListener {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    downloadFile(args.idSuratMasuk)
                    return@setOnClickListener
                }
                if(Constants.hasWriteExternalStoragePermission(requireContext())){
                    downloadFile(args.idSuratMasuk)
                }else {
                    Constants.requestPermissions(requireContext(), requireActivity())
                }
            }
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
        }catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Tidak bisa membuka dokumen karena tidak ada aplikasi pembuka PDF", Toast.LENGTH_SHORT).show()
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun observeSuratMasuk() {
        viewModel.dataSuratMasuk.observe(viewLifecycleOwner) { data ->
            if(data!= null){
                setupRvFile(data)
                binding.apply {
                    
                    btnCetakDisposisi.isVisible = data.statusSurat == "Terdisposisi"
                    tvNoSurat.text = data.noSurat
                    tvInstansiPengirim.text = data.instansiPengirim
                    tvPerihal.text = data.perihal
                    tvKategoriSurat.text = data.kategoriSurat
                    tvTglSuratMasukDibuat.text =Constants.showDate(data.tglSurat, true)
                    tvTglSuratMasukDiterima.text = data.tglSuratDiinput?.let { Constants.showDate(it, false) }
                    tvLabelSuratDiinput.text = "Surat diinput oleh ${data.namaPenginput}"
                    if (data.statusSurat == StatusSuratMasuk.Terdisposisi.name) {
                        tvCatatanDisposisi.text = data.catatanDisposisi
                        tvSifatDisposisi.text = data.sifatDisposisi
                        tvTerdisposisi.text = "${resources.getText(R.string.keterangan_surat_masuk_terdisposisi)} ${data.namaPendisposisi}"
                        cardTerdisposisi.visibility = View.VISIBLE
                        tvTglSuratMasukTerdisposisi.visibility = View.VISIBLE
                        tvTglSuratMasukTerdisposisi.text =
                        data.tglDisposisi?.let { Constants.showDate(it, false) }
                        data.penerimaDisposisi?.forEach {penerima ->
                            val chip = Chip(requireContext())
                            chip.text = penerima
                            chip.setOnClickListener { Constants.toastNormal(requireContext(), penerima) }
                            chip.setChipBackgroundColorResource(R.color.teal_700)
                            chip.setTextColor(resources.getColor(R.color.white))
                            chipDisposisiTertuju.addView(chip)
                        }
                    } else {
                        cardTerdisposisi.visibility = View.GONE
                        tvTglSuratMasukTerdisposisi.visibility = View.GONE
                    }
                }
            }
            
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoad ->
            if (isLoad != null) {
                binding.pbTambahDisposisi.visibility = if (isLoad) View.VISIBLE else View.GONE
                binding.scrollTambahDisposisi.visibility = if (isLoad) View.GONE else View.VISIBLE
//                binding.btnCetakDisposisi.visibility = if (isLoad) View.GONE else View.VISIBLE
            } else {
                binding.pbTambahDisposisi.visibility = View.GONE
                binding.scrollTambahDisposisi.visibility = View.GONE
//                binding.btnCetakDisposisi.visibility = View.GONE
            }
        }
    }
    
    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Terjadi Error")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Kembali") { _, _ ->
                        navC.popBackStack()
                    }
                    .show()
            }
        }
    }
    
    private fun setupRvFile(data: SuratMasuk) {
        adapterFile =
            RvFileSuratAdapter(data.fileSurat?.map { it.pathFile.replace("localhost", Constants.IP_ADDRESS) } ?: listOf())
        binding.rvFileSurat.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFileSurat.adapter = adapterFile
        adapterFile.setOnItemClicked(object : RvFileSuratAdapter.OnItemClickListener {
            override fun onImageClicked(linkFile: String) {
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_preview_file)
                dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
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
    
    private fun downloadFile(idSurat: Int) {
        try {
            val path = "disposisi_${idSurat}_${System.currentTimeMillis()}.pdf"
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), path)
            if (file.exists()) {
                file.delete()
            }
            val manager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse("${Constants.BASE_URL}cetak_disposisi/$idSurat")
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