package com.daylantern.suratatmaluhur.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.adapters.RvPreviewFileAdapter
import com.daylantern.suratatmaluhur.databinding.BottomSheetInstansiBinding
import com.daylantern.suratatmaluhur.databinding.DialogPickImageBinding
import com.daylantern.suratatmaluhur.databinding.FragmentTambahSuratKeluarBinding
import com.daylantern.suratatmaluhur.models.FileSuratModel
import com.daylantern.suratatmaluhur.viewmodels.TambahSuratKeluarViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@AndroidEntryPoint
class TambahSuratKeluarFragment : Fragment() {
    
    private lateinit var binding: FragmentTambahSuratKeluarBinding
    private lateinit var cameraProviderResult: ActivityResultLauncher<String>
    private lateinit var getImage: ActivityResultLauncher<String>
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private lateinit var navC: NavController
    private lateinit var previewFileAdapter: RvPreviewFileAdapter
    private var listFile: MutableList<FileSuratModel> = mutableListOf()
    private val viewModel: TambahSuratKeluarViewModel by viewModels()
    @Inject lateinit var sharedPref: SharedPreferences
    private lateinit var bottomSheet: BottomSheetDialog
    private var listPegawai = listOf<String>()
    private var listTembusan = arrayListOf<String>()
    private var selectedIndex = 0
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        binding = FragmentTambahSuratKeluarBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheet = BottomSheetDialog(requireContext())
        navC = Navigation.findNavController(view)
        viewModel.getInstansi()
        viewModel.getPegawai()
        viewModel.getKategori()
        viewModel.getPegawaiById(sharedPref.getInt(Constants.PREF_ID_PEGAWAI, 0))
        observePegawaiBagian()
        setupRVPreviewFile()
        setupIsiSurat()
        previewFileListener()
        observeInstansi()
        observeResult()
        observePegawai()
        observeKategori()
        observeErrorMessageBottomSheet()
        
        binding.apply {
            //izin kamera
            cameraProviderResult =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                    if (permissionGranted) navC.navigate(R.id.cameraFragment)
                    else Constants.toastWarning(requireContext(),"Dibutuhkan izin kamera untuk menggunakan fitur ini")
                }
            //hasil dari camera fragment
            navC.currentBackStackEntry?.savedStateHandle?.getLiveData<File>(Constants.KEY_PHOTO)
                ?.observe(viewLifecycleOwner) {
                    val uri = Uri.fromFile(it)
                    val bitmap = context?.getBitmap(uri)
                    if (bitmap != null) { previewFileAdapter.addItem(FileSuratModel(bitmap, it)) }
                }
            //hasil dari gallery
            getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    val bitmap = context?.getBitmap(uri)
                    val file = createFileFromUri(uri, requireContext())
                    if (bitmap != null && file != null) previewFileAdapter.addItem(FileSuratModel(bitmap, file))
                    else Constants.toastWarning(requireContext(), "File tidak bisa ditemukan/kosong")
                }
            }
            
            Constants.textChangedListener(tilPerihal)
            Constants.textChangedListener(tilInstansiPenerima)
            inputNamaPenerima.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tilJabatanPenerima.isErrorEnabled = false
                    tilNamaPenerima.isErrorEnabled = false
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            inputJabatanPenerima.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tilNamaPenerima.isErrorEnabled = false
                    tilJabatanPenerima.isErrorEnabled = false
                }
                override fun afterTextChanged(p0: Editable?) {}
            })
            btnPilihTembusan.setOnClickListener { showBottomSheetTembusan() }
            btnPilihPenandatangan.setOnClickListener { showSelectDialog() }
            btnLampiran.setOnClickListener { choosePickImage() }
            btnTambahInstansiPenerima.setOnClickListener { bottomSheetInstansi() }
            btnTambahKodeKategori.setOnClickListener { bottomSheetKategori() }
            btnSimpanSuratKeluar.setOnClickListener { saveKonsepSurat() }
        }
    }
    
    private fun showBottomSheetTembusan() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Tambah Tembusan")
        val input = TextInputEditText(requireContext())
        input.setTextColor(ColorStateList.valueOf(requireContext().resources.getColor(R.color.black)))
        input.hint = "Isi pihak penerima tembusan"
        input.setBackgroundResource(0)
        dialog.setView(input)
        dialog.setPositiveButton("Tambah"){p0, _ ->
            val tembusan = input.text.toString().trim()
            if(tembusan.isEmpty()){
                Constants.toastWarning(requireContext(), "Tembusan masih kosong, silahkan isi")
                return@setPositiveButton
            }
            listTembusan.add(tembusan)
            val chip = Chip(requireContext())
            chip.text = tembusan
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                listTembusan.remove(tembusan)
                binding.chipTembusan.removeView(chip)
            }
            binding.chipTembusan.addView(chip)
            p0.dismiss()
        }
        dialog.setNeutralButton("Batal"){p0,_ -> p0.cancel()}
        dialog.show()
    }
    
    
    
    private fun setupIsiSurat() {
        binding.apply {
            btnUndo.setOnClickListener { editorSuratKeluar.undo() }
            btnRedo.setOnClickListener { editorSuratKeluar.redo() }
            btnIndent.setOnClickListener { editorSuratKeluar.setIndent() }
            btnOutdent.setOnClickListener { editorSuratKeluar.setOutdent() }
            btnBold.setOnClickListener { editorSuratKeluar.setBold() }
            btnItalic.setOnClickListener { editorSuratKeluar.setItalic() }
            btnUnderline.setOnClickListener { editorSuratKeluar.setUnderline() }
            btnOrderedNumber.setOnClickListener { editorSuratKeluar.setNumbers() }
            btnUnorderedBullet.setOnClickListener { editorSuratKeluar.setBullets() }
            btnAlignLeft.setOnClickListener { editorSuratKeluar.setAlignLeft() }
            btnAlignCenter.setOnClickListener { editorSuratKeluar.setAlignCenter() }
            btnAlignRight.setOnClickListener { editorSuratKeluar.setAlignRight() }
            editorSuratKeluar.apply {
                setEditorHeight(200)
                setEditorFontSize(14)
                setEditorFontColor(Color.BLACK)
                setPadding(10, 10, 10, 10)
                setPlaceholder("Isi surat di sini")
            }
        }
    }
    
    private fun showSelectDialog(){
        val dialog = AlertDialog.Builder(requireContext())
        dialog.apply {
            setTitle("Pilih Penandatangan")
            setSingleChoiceItems(listPegawai.toTypedArray(), selectedIndex){ _, which ->
                selectedIndex = which
            }
            setNeutralButton("Batal"){dialog, _ -> dialog.cancel() }
            setPositiveButton("Selesai") {dialog, _ ->
                if(listPegawai.isNotEmpty()){
                    binding.chipPenandatangan.removeAllViews()
                    val chip = Chip(requireContext())
                    chip.text = listPegawai[selectedIndex]
                    binding.chipPenandatangan.addView(chip)
                }
                dialog.dismiss()
            }
            show()
        }
    }
    
    private fun observeResult() {
        viewModel.result.observe(viewLifecycleOwner){result ->
            if(result?.status == 200){
                Constants.toastSuccess(requireContext(), result.message!!)
                val action = TambahSuratKeluarFragmentDirections.actionTambahSuratKeluarFragmentToDetailSuratKeluarFragment(result.data)
                navC.navigate(action)
                viewModel.clear()
            }else {result?.message?.let { Constants.toastWarning(requireContext(), it) } }
        }
    }
    
    private fun saveKonsepSurat() {
        binding.apply {
            val perihal = inputPerihal.text?.trim().toString()
            val instansiPenerima = optionInstansiPenerima.text?.trim().toString()
            val kategori = optionKodeKategori.text?.trim().toString()
            val namaPenerima = inputNamaPenerima.text?.trim().toString()
            val jabatanPenerima = inputJabatanPenerima.text?.trim().toString()
            val penandatangan = listPegawai[selectedIndex]
            val isiSurat = editorSuratKeluar.html
            val idPenginput = sharedPref.getInt(Constants.PREF_ID_PEGAWAI, 0)
            val tembusan = listTembusan.joinToString(",")
            
            if(kategori.isEmpty()){
                tilKodeKategori.error = "Kategori surat masih kosong, silahkan isi"
                return
            }
            if (perihal.isEmpty()) {
                tilPerihal.error = "Perihal surat masih kosong, silahkan isi"
                return
            }
            if (instansiPenerima.isEmpty()) {
                tilInstansiPenerima.error = "Instansi belum dipilih"
                return
            }
            if (namaPenerima.isEmpty() && jabatanPenerima.isEmpty()) {
                tilNamaPenerima.error = "Salah satu dari nama/jabatan penerima wajib diisi"
                tilJabatanPenerima.error = "Salah satu dari nama/jabatan penerima wajib diisi"
                return
            }
            if (chipPenandatangan.isEmpty()) {
                Constants.toastWarning(requireContext(), "Belum memilih penandatangan")
                return
            }
            if (editorSuratKeluar.html.isNullOrEmpty()) {
                Constants.toastWarning(requireContext(), "Isi surat masih kosong, silahkan isi")
                return
            }
            Constants.alertDialog(requireContext(), "Buat Surat Keluar",
                "Apakah anda yakin ingin membuat konsep surat keluar?",
            "Batal", {p0,_ -> p0.cancel()},
            "Buat", {p0,_ ->
                    viewModel.createKonsepSurat(
                        perihal, isiSurat, idPenginput, penandatangan.substringBefore("-").trim(),
                        instansiPenerima, namaPenerima, jabatanPenerima,
                        kategori.substringBefore("-").trim(), tembusan, listFile.toList())
                    p0.dismiss()
                })
        }
    }
    
    private fun observeErrorMessageBottomSheet() {
        viewModel.operationMessage.observe(viewLifecycleOwner) { result ->
            if(result == null) return@observe
            if (result.status == 200) Constants.toastSuccess(requireContext(), result.message)
            else Constants.toastWarning(requireContext(), result.message)
        }
    }
    
    private fun observePegawaiBagian(){
        viewModel.kodeBagianPegawai.observe(viewLifecycleOwner) {
            if (it != null) {
                observePegawai()
            }
        }
    }
    
    private fun observePegawai() {
        viewModel.listPegawai.observe(viewLifecycleOwner) { list ->
            listPegawai = list.filter { it.levelAkses == "Kepala Bagian" && it.kodeBagian == viewModel.kodeBagianPegawai.value }
                .map { "${it.nama} - ${it.jabatan}" }
        }
    }
    
    private fun observeKategori() {
        viewModel.dataKategori.observe(viewLifecycleOwner) { list ->
            binding.optionKodeKategori.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { "${it.kodeKategori} - ${it.deskripsi}" }))
        }
    }
    
    private fun observeInstansi() {
        viewModel.dataInstansi.observe(viewLifecycleOwner) { list ->
            binding.optionInstansiPenerima.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { it.namaInstansi })
            )
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
            Constants.textChangedListener(tilNamaInstansi)
            btnUbahInstansi.text = "Tambah Instansi"
            btnBatalUbahInstansi.setOnClickListener { bottomSheet.cancel() }
            btnUbahInstansi.setOnClickListener {
                val namaInstansi = inputNamaInstansi.text?.trim().toString()
                val alamatInstansi = inputAlamatInstansi.text?.trim().toString()
                if (namaInstansi.isEmpty()) {
                    tilNamaInstansi.error = "Nama Instansi masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                Constants.alertDialog(requireContext(), "Tambah Instansi",
                    "Apakah Anda yakin ingin menambah instansi ini?",
                    "Batal", { p0, _ -> p0.cancel() },
                    "Tambah", { p0, _ ->
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
            Constants.textChangedListener(tilNamaInstansi)
            Constants.textChangedListener(tilAlamatInstansi)
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
                Constants.alertDialog(requireContext(), "Tambah Kategori",
                    "Apakah Anda yakin ingin menambah kategori ini?",
                    "Batal", { p0, _ -> p0.cancel() },
                    "Tambah", { p0, _ ->
                        viewModel.addKategori(kodeKategori, deskripsi)
                        p0.dismiss()
                        bottomSheet.dismiss()
                    })
            }
        }
    }
    
    private fun choosePickImage() {
        val dialogBinding = DialogPickImageBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(dialogBinding.root)
        bottomSheet.show()
        dialogBinding.apply {
            cardCamera.setOnClickListener {
                bottomSheet.dismiss()
                cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            }
            cardGambar.setOnClickListener {
                bottomSheet.dismiss()
                getImage.launch("image/*")
            }
//            cardPdf.setOnClickListener {
//                bottomSheet.dismiss()
//                getImage.launch("application/pdf")
//            }
        }
    }
    
    private fun Context.getBitmap(uri: Uri): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(this.contentResolver, uri)
        )
        else MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    
    private fun setupRVPreviewFile() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        previewFileAdapter = RvPreviewFileAdapter(listFile)
        binding.rvLampiran.layoutManager = layoutManager
        binding.rvLampiran.adapter = previewFileAdapter
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
}