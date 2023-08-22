package com.daylantern.suratatmaluhur

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.adapters.RvPreviewFileAdapter
import com.daylantern.suratatmaluhur.databinding.FragmentTambahSuratKeluarBinding
import com.daylantern.suratatmaluhur.models.FileSuratModel
import com.daylantern.suratatmaluhur.viewmodels.EditSuratKeluarViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditSuratKeluarFragment : Fragment() {
    
    private lateinit var binding: FragmentTambahSuratKeluarBinding
    private lateinit var cameraProviderResult: ActivityResultLauncher<String>
    private lateinit var getImage: ActivityResultLauncher<String>
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private lateinit var navC: NavController
    private lateinit var previewFileAdapter: RvPreviewFileAdapter
    private val args: EditSuratKeluarFragmentArgs by navArgs()
    private var listTembusan = arrayListOf<String>()
    private var listFile: MutableList<FileSuratModel> = mutableListOf()
    private val viewModel: EditSuratKeluarViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTambahSuratKeluarBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navC = Navigation.findNavController(view)
        val data = args.dataSurat
        binding.apply {
            llLampiran.isVisible = false
            tvLabelPenandatangan.isVisible = false
            btnPilihPenandatangan.isVisible = false
            inputPerihal.setText(data.perihal)
            inputNamaPenerima.setText(data.namaPenerima)
            inputJabatanPenerima.setText(data.jabatanPenerima)
            optionKodeKategori.setText(data.kategoriSurat)
            optionInstansiPenerima.setText(data.instansiPenerima)
            editorSuratKeluar.html = data.isiSurat
            listTembusan.addAll(data.tembusan)
            btnPilihTembusan.setOnClickListener { showBottomSheetTembusan() }
            tembusanChip()
            viewModel.getInstansi()
            viewModel.getKategori()
            observeInstansi()
            observeKategori()
            observeResult()
            setupRVPreviewFile()
            setupIsiSurat()
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
            btnSimpanSuratKeluar.text = "Ubah"
            btnSimpanSuratKeluar.setOnClickListener {
                saveKonsepSurat()
            }
        }
    }
    
    private fun saveKonsepSurat() {
        binding.apply {
            val perihal = inputPerihal.text?.trim().toString()
            val instansiPenerima = optionInstansiPenerima.text?.trim().toString()
            val kategori = optionKodeKategori.text?.trim().toString()
            val namaPenerima = inputNamaPenerima.text?.trim().toString()
            val jabatanPenerima = inputJabatanPenerima.text?.trim().toString()
            val isiSurat = editorSuratKeluar.html
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
            if (editorSuratKeluar.html.isNullOrEmpty()) {
                Constants.toastWarning(requireContext(), "Isi surat masih kosong, silahkan isi")
                return
            }
//            Log.d("DEBUG", "tembusan: $tembusan")
//            Log.d("DEBUG", "instansi: $instansiPenerima")
//            Log.d("DEBUG", "kategori: ${kategori.substringBefore("-").trim()}")
            Constants.alertDialog(requireContext(), "Ubah Surat Keluar",
                "Apakah anda yakin ingin mengubah konsep surat keluar?",
                "Batal", {p0,_ -> p0.cancel()},
                "Ubah", {p0,_ ->
                    viewModel.editSurat(
                        args.dataSurat.idSurat, perihal, isiSurat, instansiPenerima,
                        namaPenerima, jabatanPenerima, kategori.substringBefore("-").trim(),
                        tembusan)
                    p0.dismiss()
                })
        }
    }
    
    private fun observeResult() {
        viewModel.result.observe(viewLifecycleOwner){result ->
            if(result?.status == 200){
                Constants.toastSuccess(requireContext(), result.message!!)
                navC.popBackStack()
                viewModel.clear()
            }else {result?.message?.let { Constants.toastWarning(requireContext(), it) } }
        }
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
    
    private fun setupRVPreviewFile() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        previewFileAdapter = RvPreviewFileAdapter(listFile)
        binding.rvLampiran.layoutManager = layoutManager
        binding.rvLampiran.adapter = previewFileAdapter
    }
    
    private fun tembusanChip(tembusan: String? = null){
        if(tembusan.isNullOrEmpty()){
            listTembusan.forEach {data ->
                val chip = Chip(requireContext())
                chip.text = data
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    listTembusan.remove(data)
                    binding.chipTembusan.removeView(chip)
                }
                binding.chipTembusan.addView(chip)
            }
        }else {
            listTembusan.add(tembusan)
            val chip = Chip(requireContext())
            chip.text = tembusan
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                listTembusan.remove(tembusan)
                binding.chipTembusan.removeView(chip)
            }
            binding.chipTembusan.addView(chip)
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
            tembusanChip(tembusan)
            p0.dismiss()
        }
        dialog.setNeutralButton("Batal"){p0,_ -> p0.cancel()}
        dialog.show()
    }
    
    private fun observeInstansi() {
        viewModel.dataInstansi.observe(viewLifecycleOwner) { list ->
            binding.optionInstansiPenerima.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { it.namaInstansi })
            )
        }
    }
    
    private fun observeKategori() {
        viewModel.dataKategori.observe(viewLifecycleOwner) { list ->
            binding.optionKodeKategori.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { "${it.kodeKategori} - ${it.deskripsi}" })
            )
        }
    }
    
}