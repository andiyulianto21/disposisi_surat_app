package com.daylantern.arsipsuratpembinaan.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
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
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.Constants.KEY_PHOTO
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.adapters.RvPreviewFileAdapter
import com.daylantern.arsipsuratpembinaan.databinding.FragmentTambahSuratMasukBinding
import com.daylantern.arsipsuratpembinaan.models.FileSuratModel
import com.daylantern.arsipsuratpembinaan.models.PilihData
import com.daylantern.arsipsuratpembinaan.viewmodels.TambahSuratMasukViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var sifatArrayAdapter: ArrayAdapter<String>
    private val viewModel: TambahSuratMasukViewModel by viewModels()
    private lateinit var calendar: Calendar
    private lateinit var navC: NavController
    private var message: String? = null
    private var listFile: MutableList<FileSuratModel> = mutableListOf()
    private lateinit var cameraProviderResult: ActivityResultLauncher<String>
    private lateinit var getImage: ActivityResultLauncher<String>

    @Inject
    lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTambahSuratMasukBinding.inflate(layoutInflater)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permissionGranted->
            if(permissionGranted){
                navC.navigate(R.id.cameraFragment)
            }else {
                Snackbar.make(binding.root,"The camera permission is required", Snackbar.LENGTH_SHORT).show()
            }
        }

        getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val bitmap = context?.getBitmap(uri)
                val file = createFileFromUri(uri, requireContext())
                if(bitmap!= null && file != null){
                    previewFileAdapter.addItem(FileSuratModel(bitmap, file))
                    binding.tvTotalFileTerpilih.text = "${listFile.size} Terpilih"
                }else {
                    Toast.makeText(requireContext(), "bitmap/file kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        navC = Navigation.findNavController(view)
        viewModel.fetchInstansi()
        viewModel.fetchSifat()
        (activity as AppCompatActivity).supportActionBar?.title = "Tambah Surat Masuk"

        calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        binding.inputTglSurat.setText("$day ${Constants.convertMonth(month)} $year")
        selectedInstansi = mutableListOf()
        dialogInstansi = Dialog(requireContext())

        binding.apply {
            inputTglSurat.setOnClickListener {
                showDatePicker(calendar)
            }
            btnTambahInstansiPengirim.setOnClickListener {
                showBottomSheetDialog("Tambah Instansi Baru")
            }
            btnTambahSifatSurat.setOnClickListener {
                showBottomSheetDialog("Tambah Sifat Surat Baru")
            }
            btnPilihFile.setOnClickListener {
                choosePickImage()

            }
            btnSimpanSuratMasuk.setOnClickListener {
                showConfirmationDialog()
            }
        }

        setDropdownInstansi()
        setDropdownSifat()

        viewModel.message.observe(viewLifecycleOwner) { msg ->
            if (msg != null)
                message = msg
        }

        binding.optionInstansiPengirim.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.changeValueInstansi(selectedItem)
        }

        binding.optionSifatSurat.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.changeValueSifat(selectedItem)
        }
        setupRVPreviewFile()

        //hasil dari camera fragment
        navC.currentBackStackEntry?.savedStateHandle?.getLiveData<File>(KEY_PHOTO)?.observe(viewLifecycleOwner){
            val uri = Uri.fromFile(it)
            val bitmap = context?.getBitmap(uri)
            if (bitmap != null) {
                previewFileAdapter.addItem(FileSuratModel(bitmap, it))
                binding.tvTotalFileTerpilih.text = "${listFile.size} Terpilih"
//                listFile.add(FileSuratModel(bitmap, it))
            }
        }

        previewFileAdapter.setOnItemClicked(object : RvPreviewFileAdapter.Listener {
            override fun onItemClicked(image: Bitmap) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_preview_file)
                val iconDownload = dialog.findViewById<ImageView>(R.id.img_download_file)
                iconDownload.visibility = View.GONE
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val imgPreview = dialog.findViewById<ImageView>(R.id.img_preview_file)
                imgPreview.setImageBitmap(image)
                dialog.show()
            }

            @SuppressLint("SetTextI18n")
            override fun onItemRemoved() {
                binding.tvTotalFileTerpilih.text = "${listFile.size} Terpilih"
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

    fun getFileName(uri: Uri, context: Context): String {
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

    private fun choosePickImage() {
        val dialogPickImage = BottomSheetDialog(requireContext())
        dialogPickImage.setContentView(R.layout.dialog_pick_image)
        val cardCamera = dialogPickImage.findViewById<CardView>(R.id.card_camera)
        val cardGallery = dialogPickImage.findViewById<CardView>(R.id.card_gallery)
        dialogPickImage.show()

        cardCamera?.setOnClickListener{
            dialogPickImage.dismiss()
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }
        cardGallery?.setOnClickListener {
            dialogPickImage.dismiss()
            getImage.launch("image/*")
        }
    }

    private fun setupRVPreviewFile(){
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        previewFileAdapter = RvPreviewFileAdapter(listFile)
        binding.rvFileSurat.layoutManager = layoutManager
        binding.rvFileSurat.adapter = previewFileAdapter
    }

    private fun Context.getBitmap(uri: Uri): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri))
        else MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

    private fun setDropdownInstansi() {
        viewModel.dataInstansi.observe(viewLifecycleOwner) { list ->
            binding.optionInstansiPengirim.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { it.namaInstansi })
            )
        }

        viewModel.selectedInstansi.observe(viewLifecycleOwner) { nama ->
            binding.optionInstansiPengirim.setText(nama, false)
        }
    }

    private fun setDropdownSifat() {
        viewModel.dataSifat.observe(viewLifecycleOwner) { list ->
            sifatArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { it.sifatSurat })
            binding.optionSifatSurat.setAdapter(
                sifatArrayAdapter
            )
        }

        viewModel.selectedSifat.observe(viewLifecycleOwner) { sifat ->
            binding.optionSifatSurat.setText(sifat, false);
        }
    }

    private fun showBottomSheetDialog(message: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_tambah_data)
        val tvTambahData = bottomSheetDialog.findViewById<TextView>(R.id.tv_judul_tambah_data)
        val tvError = bottomSheetDialog.findViewById<TextView>(R.id.tv_error_tambah_data)
        val tilTambahData = bottomSheetDialog.findViewById<TextInputLayout>(R.id.til_tambah_data)
        val inputTambahData =
            bottomSheetDialog.findViewById<TextInputEditText>(R.id.input_tambah_data)
        val imgTambahData = bottomSheetDialog.findViewById<ImageView>(R.id.img_tambah_data)
        val imgError = bottomSheetDialog.findViewById<ImageView>(R.id.img_error_sign)


        tvTambahData?.text = message
        tilTambahData?.requestFocus()
        bottomSheetDialog.show()
        imgTambahData?.setOnClickListener {
            if (message.contains("instansi", true)) {
                viewModel.insertInstansi(inputTambahData?.text?.trim().toString(), "")
            } else {
                viewModel.insertSifat(inputTambahData?.text?.trim().toString())
            }
            viewModel.errorBottomSheet.observe(viewLifecycleOwner) { error ->
                if (error.isNullOrBlank()) {
                    imgError?.visibility = View.GONE
                    tvError?.visibility = View.GONE
                    bottomSheetDialog.dismiss()
                    Log.d("error", "BERHASIL DAN KELUAR")
                } else
                    imgError?.visibility = View.VISIBLE
                tvError?.visibility = View.VISIBLE
                tvError?.text = error
            }
        }
    }

    private fun showConfirmationDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_konfirmasi)
        val imgKonfirmasiTutup = dialog.findViewById<ImageView>(R.id.img_konfirmasi_tutup)
        val btnKonfirmasiSimpan = dialog.findViewById<MaterialButton>(R.id.btn_konfirmasi_simpan)
        val tvDeskripsiKonfirmasi = dialog.findViewById<TextView>(R.id.tv_deskripsi_konfirmasi)
        val tvJudulKonfirmasi = dialog.findViewById<TextView>(R.id.tv_judul_konfirmasi)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        imgKonfirmasiTutup.setOnClickListener {
            dialog.dismiss()
        }
        btnKonfirmasiSimpan.text = "Simpan"
        tvDeskripsiKonfirmasi.text = resources.getText(R.string.desk_konfirmasi_tambah_suratmasuk)
        tvJudulKonfirmasi.text = resources.getText(R.string.judul_konfirmasi_tambah_suratmasuk)

        dialog.show()

        btnKonfirmasiSimpan.setOnClickListener {
            val noSurat = binding.inputNoSurat.text?.trim().toString()
            val perihal = binding.inputPerihal.text?.trim().toString()
            val tglSurat = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

            if (noSurat.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Form No Surat belum diisi / kosong",
                    Toast.LENGTH_LONG
                ).show()
                dialog.cancel()
                return@setOnClickListener
            }
            if (perihal.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Form perihal belum diisi / kosong",
                    Toast.LENGTH_LONG
                ).show()
                dialog.cancel()
                return@setOnClickListener
            }
            
            if(listFile.isEmpty()){
                Toast.makeText(requireContext(), "File surat masih kosong", Toast.LENGTH_SHORT).show()
                dialog.cancel()
                return@setOnClickListener
            }

            viewModel.insertSurat(
                noSurat,
                perihal,
                tglSurat,
                listFile.map { it.file.path }
            )
            viewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess == true) {
                    Toast.makeText(requireContext(), viewModel.message.value, Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    navC.navigate(R.id.menu_suratMasuk)
                } else {
                    dialog.cancel()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker(calendar: Calendar) {

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
        dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
        dpd.show()

    }

}