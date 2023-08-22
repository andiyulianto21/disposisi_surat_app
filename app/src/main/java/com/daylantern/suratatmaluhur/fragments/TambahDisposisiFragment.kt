package com.daylantern.suratatmaluhur.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.adapters.RvFileSuratAdapter
import com.daylantern.suratatmaluhur.databinding.FragmentTambahDisposisiBinding
import com.daylantern.suratatmaluhur.entities.SuratMasuk
import com.daylantern.suratatmaluhur.viewmodels.TambahDisposisiViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.util.Log
import android.view.Window
import android.widget.ArrayAdapter
import com.daylantern.suratatmaluhur.databinding.BottomSheetMultipleChoiceBinding
import javax.inject.Inject

@AndroidEntryPoint
class TambahDisposisiFragment : Fragment() {

    private lateinit var binding: FragmentTambahDisposisiBinding
    private lateinit var bottomSheetBinding: BottomSheetMultipleChoiceBinding
    private lateinit var navC: NavController
    private val viewModel: TambahDisposisiViewModel by viewModels()
    private val args: TambahDisposisiFragmentArgs by navArgs()
    private lateinit var bottomSheet: BottomSheetDialog
    private var suratMasuk: SuratMasuk? = null
    private lateinit var adapterFile: RvFileSuratAdapter
    private var listChip: MutableList<Chip> = mutableListOf()
    private lateinit var checkedItems: MutableMap<String, Boolean>
    @Inject lateinit var pref: SharedPreferences
    private var idPimpinan = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTambahDisposisiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idPimpinan = pref.getInt(Constants.PREF_ID_PEGAWAI, 0)
        navC = Navigation.findNavController(view)
        viewModel.fetchSuratMasuk(args.idSuratMasuk)
        viewModel.fetchPegawai(idPimpinan)
        observeLoading()
        observeSuratMasuk()
        observePihakTujuan()
        observeMessage()
        observePegawai()
    
        bottomSheet = BottomSheetDialog(requireContext())
        bottomSheetBinding = BottomSheetMultipleChoiceBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        
        binding.apply {
            Constants.textChangedListener(tilCatatanDisposisi)
                Constants.textChangedListener(tilSifatDisposisi)
            optionSifatDisposisi.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_item, mutableListOf("Segera", "Penting", "Biasa")))
            btnPilihTujuanDisposisi.setOnClickListener { showMultiselectDialog() }
            btnMelakukanDisposisi.setOnClickListener {
                disposisikan()
            }
        }
    }
    
    private fun observeMessage() {
        viewModel.message.observe(viewLifecycleOwner){result ->
            if(result?.status == 200) {
                Constants.toastSuccess(requireContext(), result.message)
                val action = TambahDisposisiFragmentDirections.actionTambahDisposisiFragmentToDetailSuratMasukFragment(args.idSuratMasuk)
                navC.navigate(action)
                viewModel.clear()
            }else result?.let { Constants.toastWarning(requireContext(), it.message) }
        }
    }
    
    private fun disposisikan() {
        binding.apply {
            val catatanDisposisi = inputCatatanDisposisi.text?.trim().toString()
            val sifatDisposisi = optionSifatDisposisi.text?.trim().toString()
            val idPimpinan = pref.getInt(Constants.PREF_ID_PEGAWAI, 0)
            
            if(catatanDisposisi.isEmpty()){
                tilCatatanDisposisi.error = "Catatan disposisi masih kosong, silahkan isi"
                return
            }
            if(sifatDisposisi.isEmpty()){
                tilSifatDisposisi.error = "Sifat disposisi masih kosong, silahkan isi"
                return
            }
            if(idPimpinan == 0){
                Constants.toastWarning(requireContext(), "Id anda tidak diketahui, silahkan logout dan login kembali")
                return
            }
            if(chipDisposisiTertuju.childCount < 1){
                Constants.toastWarning(requireContext(), "Penerima/tujuan disposisi masih kosong, silahkan pilih")
                return
            }
            val listIdPenerima = mutableListOf<Int>()
            for (i in 0 until chipDisposisiTertuju.childCount) {
                val chip: Chip = chipDisposisiTertuju.getChildAt(i) as Chip
                Log.d("debug", "disposisikan id pegawai: ${chip.text.toString().substringBefore("-").trim()}")
                viewModel.getIdPegawai(chip.text.toString().substringBefore("-").trim())
                    ?.let { listIdPenerima.add(it) }
            }
            Constants.alertDialog(requireContext(), "Disposisikan","Apakah anda yakin ingin mendisposisikan surat ini?",
                "Batal", {p0,_ -> p0.cancel()},
                "Disposisi", {p0,_ ->
                    viewModel.addDisposisi(
                        catatanDisposisi,
                        listIdPenerima.joinToString(","),
                        sifatDisposisi,
                        idPimpinan
                    )
                    Log.d("debug", "disposisikan id pegawai: ${listIdPenerima.joinToString(",")}")
                    p0.dismiss()
                })
        }
    }

    private fun observeSuratMasuk() {
        viewModel.suratMasuk.observe(viewLifecycleOwner) {surat ->
            if (surat != null) {
                suratMasuk = surat
                setupRvFile(surat)
                binding.apply {
                    tvNoSurat.text = surat.noSurat
                    tvPerihal.text = surat.perihal
                    tvKategoriSurat.text = surat.kategoriSurat
                    tvLabelSuratDiinput.text = "Surat diinput oleh ${surat.namaPenginput}"
                    tvTglSuratMasukDibuat.text = Constants.showDate(surat.tglSurat, true)
                    tvTglSuratMasukDiterima.text =
                        surat.tglSuratDiinput?.let {Constants.showDate(it, false) }
                    tvInstansiPengirim.text = surat.instansiPengirim
                }
            }
        }
    }
    
    private fun observePegawai(){
        viewModel.listPegawai.observe(viewLifecycleOwner) { list ->
            checkedItems = mutableMapOf()
            val listExcludePimpinan = list.filter { it.idPegawai != idPimpinan }.filter { it.levelAkses != Constants.LEVEL_PIMPINAN }
            for (item in listExcludePimpinan) {
                checkedItems["${item.nama} - ${item.jabatan}"] = false
            }
        }
    }

    private fun observePihakTujuan(){
        viewModel.pihakTujuan.observe(viewLifecycleOwner) { list ->
            list.forEach { text ->
                val chip = Chip(requireContext())
                chip.text = text
                chip.isCloseIconVisible = true
                binding.chipDisposisiTertuju.addView(chip)
                listChip.add(chip)
            }
        }
    }

    private fun observeLoading(){
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoad ->
            if(isLoad != null){
                binding.pbTambahDisposisi.visibility = if (isLoad) View.VISIBLE else View.GONE
                binding.scrollTambahDisposisi.visibility = if (isLoad) View.GONE else View.VISIBLE
//                binding.btnMelakukanDisposisi.visibility = if (isLoad) View.GONE else View.VISIBLE
            }else {
                binding.pbTambahDisposisi.visibility = View.GONE
                binding.scrollTambahDisposisi.visibility = View.GONE
                binding.btnMelakukanDisposisi.visibility = View.GONE
            }
        }
    }
    
    private fun setupRvFile(data: SuratMasuk) {
        adapterFile = RvFileSuratAdapter(data.fileSurat?.map { it.pathFile.replace("localhost", Constants.IP_ADDRESS) } ?: listOf())
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
                val imgFile = dialog.findViewById<ImageView>(R.id.img_preview_file)
                Glide.with(requireContext()).load(linkFile).into(imgFile)
                val imgDownload = dialog.findViewById<ImageView>(R.id.img_download_file)
                imgDownload.setOnClickListener {
                    if(hasWriteExternalStoragePermission()){
                        downloadFile(linkFile)
                        Toast.makeText(requireContext(), "Downloading", Toast.LENGTH_SHORT).show()
                    }else {
                        requestPermissions()
                    }
                }
            }
        })
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
    
    private fun downloadFile(linkFile: String){
        try {
            val manager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(linkFile)
            val request = DownloadManager.Request(uri)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}.jpg")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            manager.enqueue(request)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showMultiselectDialog(){
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Pilih Penerima Disposisi")
        dialog.setMultiChoiceItems(checkedItems.map { it.key }.toTypedArray(), checkedItems.map { it.value }.toBooleanArray()){ _, which, isChecked ->
            val item = checkedItems.map { it.key }.toList()[which]
            checkedItems[item] = isChecked
        }
        dialog.setPositiveButton("Selesai") {dialog, _ ->
            binding.chipDisposisiTertuju.removeAllViews()
            val selectedItems = mutableListOf<String>()
            for ((item, isChecked) in checkedItems) {
                if (isChecked) {
                    selectedItems.add(item)
                    val chip = Chip(requireContext())
                    chip.text = item
                    binding.chipDisposisiTertuju.addView(chip)
                }
            }
            dialog.dismiss()
        }
        dialog.setNeutralButton("Batal"){dialog, _ -> dialog.cancel() }
        dialog.show()
    }
}