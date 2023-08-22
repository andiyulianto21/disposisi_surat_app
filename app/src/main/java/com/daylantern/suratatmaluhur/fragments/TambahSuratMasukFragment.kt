package com.daylantern.suratatmaluhur.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.Constants.KEY_PHOTO
import com.daylantern.suratatmaluhur.Constants.alertDialog
import com.daylantern.suratatmaluhur.Constants.textChangedListener
import com.daylantern.suratatmaluhur.Constants.toastSuccess
import com.daylantern.suratatmaluhur.Constants.toastWarning
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.adapters.RvPreviewFileAdapter
import com.daylantern.suratatmaluhur.databinding.BottomSheetInstansiBinding
import com.daylantern.suratatmaluhur.databinding.DialogPickImageBinding
import com.daylantern.suratatmaluhur.databinding.FragmentTambahSuratMasukBinding
import com.daylantern.suratatmaluhur.models.FileSuratModel
import com.daylantern.suratatmaluhur.models.PilihData
import com.daylantern.suratatmaluhur.viewmodels.TambahSuratMasukViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TambahSuratMasukFragment : Fragment() {
    
    private lateinit var binding: FragmentTambahSuratMasukBinding
    private lateinit var dialogInstansi: Dialog
    private lateinit var selectedInstansi: MutableList<PilihData>
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private lateinit var previewFileAdapter: RvPreviewFileAdapter
    private var listFile: MutableList<FileSuratModel> = mutableListOf()
    private val viewModel: TambahSuratMasukViewModel by viewModels()
    private lateinit var calendar: Calendar
    private lateinit var navC: NavController
    private var message: String? = null
    private lateinit var cameraProviderResult: ActivityResultLauncher<String>
    private lateinit var getImage: ActivityResultLauncher<String>
    @Inject lateinit var apiService: ApiService
    @Inject lateinit var sharedPref: SharedPreferences
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        binding = FragmentTambahSuratMasukBinding.inflate(layoutInflater)
        return binding.root
    }
    
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        navC = Navigation.findNavController(view)
        viewModel.getInstansi()
        viewModel.getKategori()
        observeInstansi()
        observeLoadingTambahSurat()
        observeErrorMessage()
        observeErrorMessageBottomSheet()
        observeAddMessage()
        observeKategori()
        setupRVPreviewFile()
        previewFileListener()
        
        calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        selectedInstansi = mutableListOf() 
        dialogInstansi = Dialog(requireContext())
        
        binding.apply {
            textChangedListener(tilNoSurat)
            textChangedListener(tilTglSurat)
            textChangedListener(tilInstansiPengirim)
            textChangedListener(tilPerihal)
            textChangedListener(tilKategori)
            
            //izin kamera
            cameraProviderResult =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                    if (permissionGranted) navC.navigate(R.id.cameraFragment)
                    else toastWarning(requireContext(),"Dibutuhkan izin kamera untuk menggunakan fitur ini")
                }
            //hasil dari camera fragment
            navC.currentBackStackEntry?.savedStateHandle?.getLiveData<File>(KEY_PHOTO)
                ?.observe(viewLifecycleOwner) {
                    val uri = Uri.fromFile(it)
                    val bitmap = context?.getBitmap(uri)
                    if (bitmap != null) {
                        previewFileAdapter.addItem(FileSuratModel(bitmap, it))
                    }
                }
            //hasil dari gallery
            getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    val bitmap = context?.getBitmap(uri)
                    val file = createFileFromUri(uri, requireContext())
                    if (bitmap != null && file != null) previewFileAdapter.addItem(FileSuratModel(bitmap, file))
                    else toastWarning(requireContext(), "File tidak bisa ditemukan/kosong")
                }
            }
            inputTglSurat.setText("$day ${Constants.convertMonth(month)} $year")
            inputTglSurat.setOnClickListener { showDatePicker() }
            btnTambahInstansiPengirim.setOnClickListener { bottomSheetInstansi() }
            btnTambahKategori.setOnClickListener { bottomSheetKategori() }
            btnPilihFile.setOnClickListener { dialogPilihFile() }
            btnSimpanSuratMasuk.setOnClickListener { confirmationDialog() }
        }
    }
    
    private fun observeKategori() {
        viewModel.dataKategori.observe(viewLifecycleOwner){list ->
            binding.optionKategori.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { "${it.kodeKategori} - ${it.deskripsi}" } )
            )
        }
        
    }
    
    private fun observeInstansi() {
        viewModel.dataInstansi.observe(viewLifecycleOwner) { list ->
            binding.optionInstansiPengirim.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { it.namaInstansi })
            )
        }
    }
    
    private fun observeAddMessage() {
        viewModel.addMessage.observe(viewLifecycleOwner){result ->
            if(result==null) return@observe
            if(result.status == 200){
                val action = TambahSuratMasukFragmentDirections.actionTambahSuratMasukFragmentToDetailSuratMasukFragment(result.data)
                navC.navigate(action)
                viewModel.clear()
            }else result.message?.let { toastWarning(requireContext(), it) }
        }
    }
    
    private fun observeErrorMessageBottomSheet() {
        viewModel.operationMessage.observe(viewLifecycleOwner) { result ->
            if(result == null) return@observe
            if (result.status == 200) {
                toastSuccess(requireContext(), result.message)
                viewModel.clear()
            }
            else toastWarning(requireContext(), result.message)
        }
    }
    
    private fun observeErrorMessage() {
        viewModel.message.observe(viewLifecycleOwner) { msg ->
            if (msg != null) message = msg
        }
    }
    
    private fun observeLoadingTambahSurat() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
            binding.tvLoading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
            binding.scrollTambahSuratMasuk.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.btnSimpanSuratMasuk.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }
    
    private fun bottomSheetInstansi() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomSheetInstansiBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        bottomSheetBinding.apply {
            tilNamaInstansi.isEnabled = true
            tilAlamatInstansi.isEnabled = true
            textChangedListener(tilNamaInstansi)
            btnUbahInstansi.text = "Tambah Instansi"
            btnBatalUbahInstansi.setOnClickListener { bottomSheet.cancel() }
            btnUbahInstansi.setOnClickListener {
                val namaInstansi = inputNamaInstansi.text?.trim().toString()
                val alamatInstansi = inputAlamatInstansi.text?.trim().toString()
                if (namaInstansi.isEmpty()) {
                    tilNamaInstansi.error = "Nama Instansi masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                alertDialog(requireContext(), "Tambah Instansi",
                    "Apakah Anda yakin ingin menambah instansi ini?",
                    "Batal", {p0, _ -> p0.cancel() },
                    "Tambah", {p0, _ ->
                        viewModel.addInstansi(namaInstansi, alamatInstansi)
                        p0.dismiss()
                        bottomSheet.dismiss()
                    })
            }
        }
    }
    
    private fun bottomSheetKategori() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomSheetInstansiBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        
        bottomSheetBinding.apply {
            tilNamaInstansi.isEnabled = true
            tilAlamatInstansi.isEnabled = true
            inputAlamatInstansi.hint = ""
            tvLabelNamaInstansi.text = "Kode Kategori"
            tvLabelAlamatInstansi.text = "Deskripsi"
            btnUbahInstansi.text = "Tambah Kategori"
            textChangedListener(tilNamaInstansi)
            textChangedListener(tilAlamatInstansi)
            btnBatalUbahInstansi.setOnClickListener { bottomSheet.cancel() }
            btnUbahInstansi.setOnClickListener {
                val kodeKategori = inputNamaInstansi.text?.trim().toString()
                val deskripsi = inputAlamatInstansi.text?.trim().toString()
                if(kodeKategori.isEmpty()) tilNamaInstansi.error = "Kode kategori masih kosong, silahkan isi"
                if(deskripsi.isEmpty()) {
                    tilAlamatInstansi.error = "Deskripsi masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(kodeKategori.length > 6) {
                    tilNamaInstansi.error = "Kode kategori maksimal 5 huruf saja"
                    return@setOnClickListener
                }
                alertDialog(requireContext(), "Tambah Kategori",
                    "Apakah Anda yakin ingin menambah kategori ini?",
                    "Batal", {p0, _ -> p0.cancel() },
                    "Tambah", {p0, _ ->
                        viewModel.addKategori(kodeKategori, deskripsi)
                        p0.dismiss()
                        bottomSheet.dismiss()
                    })
            }
        }
    }
    
    private fun previewFileListener() {
        previewFileAdapter.setOnItemClicked(object : RvPreviewFileAdapter.Listener {
            override fun onItemClicked(image: Bitmap) {
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_preview_file)
                val iconDownload = dialog.findViewById<ImageView>(R.id.img_download_file)
                iconDownload.visibility = View.GONE
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.show()
                val imgPreview = dialog.findViewById<PhotoView>(R.id.img_preview_file)
                imgPreview.setImageBitmap(image)
            }
        })
    }
    
    private fun createFileFromUri(uri: Uri, context: Context): File? {
        val contentResolver = context.contentResolver
        val displayName = getFileName(uri, context)
        val mimeType = contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        val filePath = context.getExternalFilesDir(null)?.absolutePath + "/" + displayName
        
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        
        try {
            inputStream = contentResolver.openInputStream(uri)
            outputStream = FileOutputStream(filePath)
            
            if (inputStream == null) return null
            
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            
            var len = inputStream.read(buffer)
            while (len != -1) {
                outputStream.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            
            return File(filePath)
            
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        
        return null
    }
    
    private fun getFileName(uri: Uri, context: Context): String {
        var name = ""
        
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        
        cursor?.let {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            name = it.getString(nameIndex)
            cursor.close()
        }
        
        return name
    }
    
    private fun dialogPilihFile() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogPickImageBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        
        dialogBinding.apply {
            tvJudulDialog.text = "Pilih File/Lampiran Surat"
            cardCamera.setOnClickListener {
                dialog.dismiss()
                cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            }
            cardGambar.setOnClickListener {
                dialog.dismiss()
                getImage.launch("image/*")
            }
//            cardPdf.setOnClickListener {
//                dialog.dismiss()
//                getImage.launch("application/pdf")
//            }
        }
    }
    
    private fun setupRVPreviewFile() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        previewFileAdapter = RvPreviewFileAdapter(listFile)
        binding.rvFileSurat.layoutManager = layoutManager
        binding.rvFileSurat.adapter = previewFileAdapter
    }
    
    private fun Context.getBitmap(uri: Uri): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(this.contentResolver, uri))
        else MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    
    private fun confirmationDialog() {
        binding.apply {
            val idPenginput = sharedPref.getInt(Constants.PREF_ID_PEGAWAI, 0)
            val noSurat = inputNoSurat.text?.trim().toString()
            val perihal = inputPerihal.text?.trim().toString()
            val instansiPengirim = optionInstansiPengirim.text.toString().trim()
            val kategori = optionKategori.text.toString().trim()
            val tglSurat = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
                calendar.get(Calendar.DAY_OF_MONTH)
            }"
            Log.d("debug", "tgl: $tglSurat")
            if (noSurat.isEmpty()) {
                tilNoSurat.error = "No Surat belum diisi/kosong"
                return
            }
            if (binding.inputTglSurat.text.isNullOrEmpty()) {
                tilTglSurat.error ="Tanggal belum diisi/kosong"
                return
            }
            if (instansiPengirim.isEmpty()) {
                tilInstansiPengirim.error ="Instansi pengirim belum dipilih"
                return
            }
            if (kategori.isEmpty()) {
                tilKategori.error ="Kategori surat belum dipilih"
                return
            }
            if (perihal.isEmpty()) {
                tilPerihal.error = "perihal belum diisi/kosong"
                return
            }
            if (listFile.isEmpty()) {
                toastWarning(requireContext(), "File surat belum dipilih/kosong")
                return
            }
            if(idPenginput == 0){
                toastWarning(requireContext(), "Id anda tidak diketahui, silahkan logout dan login kembali")
                return
            }
            alertDialog(
                requireContext(),
                "Tambah Surat Masuk",
                "Anda yakin ingin menambahkan data surat masuk ini?",
                "Batal", { dialog, _ ->
                    dialog.cancel()
                },
                "Tambah"
            ) { dialog, _ ->
                viewModel.insertSurat(
                    noSurat,
                    perihal,
                    tglSurat,
                    listFile.toList(),
                    instansiPengirim,
                    kategori,
                    idPenginput
                )
                dialog.dismiss()
            }
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun showDatePicker() {
        val dpd = DatePickerDialog(
            requireContext(),
            { datePicker, y, mm, dd ->
                binding.inputTglSurat.setText("$dd ${Constants.convertMonth(mm)} $y")
                calendar.set(y, mm, dd)
                datePicker.updateDate(y, mm, dd)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.maxDate = System.currentTimeMillis() + 5000
        dpd.show()
    }
}