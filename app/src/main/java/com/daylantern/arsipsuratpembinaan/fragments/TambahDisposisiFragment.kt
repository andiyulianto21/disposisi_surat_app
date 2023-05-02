package com.daylantern.arsipsuratpembinaan.fragments

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.adapters.RvFileSuratAdapter
import com.daylantern.arsipsuratpembinaan.adapters.RvPilihDataAdapter
import com.daylantern.arsipsuratpembinaan.databinding.FragmentTambahDisposisiBinding
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk
import com.daylantern.arsipsuratpembinaan.viewmodels.TambahDisposisiViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject

@AndroidEntryPoint
class TambahDisposisiFragment : Fragment() {

    private lateinit var binding: FragmentTambahDisposisiBinding
    private lateinit var navC: NavController
    private val viewModel: TambahDisposisiViewModel by viewModels()
    private val args: TambahDisposisiFragmentArgs by navArgs()
    private lateinit var adapter: RvPilihDataAdapter
    private lateinit var bottomSheet: BottomSheetDialog
    private var tvDataKosong: TextView? = null
    private var imgDataKosong: ImageView? = null
    private var imgSelesaiPilih: ImageView? = null
    private var rvPilihData: RecyclerView? = null
    private var suratMasuk: SuratMasuk? = null
    private lateinit var adapterFile: RvFileSuratAdapter
    private var listChip: MutableList<Chip> = mutableListOf()

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTambahDisposisiBinding.inflate(layoutInflater)
        when(pref.getString(Constants.PREF_JABATAN, null)?.lowercase()){
            "kepala sekolah" -> switchFeatureInput(true)
            else -> switchFeatureInput(false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navC = Navigation.findNavController(view)

        viewModel.fetchSuratMasuk(args.idSuratMasuk)
        viewModel.fetchPegawai()
        setupBottomSheet()
        observeLoading()
        observeSuratMasuk()
        observePihakTujuan()
        observeIsDisposisiSuccess()

        binding.btnPilihTujuanDisposisi.setOnClickListener { showBottomSheet() }

        binding.btnMelakukanDisposisi.setOnClickListener { saveDisposisi() }
    }

    private fun switchFeatureInput(isKepsek: Boolean) {
        (activity as AppCompatActivity).supportActionBar?.title = if (isKepsek) "Disposisi Surat" else "Detail Surat Masuk"
        binding.apply {
            tilCatatanDisposisi.visibility = if (isKepsek) View.VISIBLE else View.GONE
            tvLabelCatatanDisposisi.visibility = if (isKepsek) View.VISIBLE else View.GONE
            tvLabelTujuan.visibility = if (isKepsek) View.VISIBLE else View.GONE
            btnPilihTujuanDisposisi.visibility =if (isKepsek) View.VISIBLE else View.GONE
            btnMelakukanDisposisi.visibility = if (isKepsek) View.VISIBLE else View.GONE
        }
    }

    private fun observeSuratMasuk() {
        viewModel.suratMasuk.observe(viewLifecycleOwner) {
            if (it != null) {
                suratMasuk = it
                setupRvFile(it)
                val tglMasuk = Constants.convertDateStringToCalendar(it.tglSuratMasuk, true)
                val tglDiterima = Constants.convertDateStringToCalendar(it.tglSuratDiterima, false)
                binding.apply {
                    tvSifatSurat.text = it.sifatSurat
                    tvNoSurat.text = it.noSuratMasuk
                    tvPerihal.text = it.perihal
                    tvTglSuratMasukDibuat.text = Constants.showDate(tglMasuk, true)
                    tvTglSuratMasukDiterima.text = Constants.showDate(tglDiterima, false)
                    tvInstansiPengirim.text = it.instansiPengirim
                }
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
                chip.setOnCloseIconClickListener {
                    viewModel.removeSelectedTujuan(text)
                    binding.chipDisposisiTertuju.removeView(chip)
                }
            }
        }
    }

    private fun observeIsDisposisiSuccess(){
        viewModel.isSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), viewModel.message.value, Toast.LENGTH_SHORT).show()
                navC.navigate(R.id.menu_suratMasuk)
            }
        }
    }

    private fun observeLoading(){
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoad ->
            if(isLoad){
                binding.pbTambahDisposisi.visibility = View.VISIBLE
            }else {
                binding.pbTambahDisposisi.visibility = View.GONE
            }
        }
    }

    private fun saveDisposisi() {
        val listIdPegawai = mutableListOf<Int>()
        for (i in 0 until binding.chipDisposisiTertuju.childCount) {
            val chip = binding.chipDisposisiTertuju.getChildAt(i) as Chip
            val idPegawai = viewModel.getIdPegawai(chip.text.toString())
            idPegawai?.let { listIdPegawai.add(it) }
        }
        if(binding.inputCatatanDisposisi.text.toString().isEmpty()){
            binding.tilCatatanDisposisi.error = "Catatan tidak boleh kosong"
            return
        }else {
            binding.tilCatatanDisposisi.error = null
        }
        if(listIdPegawai.isEmpty()){
            Toast.makeText(requireContext(), "Belum memilih pihak tujuan", Toast.LENGTH_LONG).show()
            return
        }
        val pihakTujuan = listIdPegawai.joinToString(", ")
        viewModel.saveDisposisi(
            suratMasuk?.idSuratMasuk!!,
            suratMasuk?.idDisposisi!!,
            binding.inputCatatanDisposisi.text?.trim().toString(),
            pihakTujuan
        )
    }

    private fun setupRvFile(data: SuratMasuk) {
        adapterFile = RvFileSuratAdapter(data.fileSurat.map { it.replace("localhost", "10.0.2.2") })
        binding.rvFileSurat.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFileSurat.adapter = adapterFile
        adapterFile.setOnItemClicked(object : RvFileSuratAdapter.OnItemClickListener {
            override fun onImageClicked(linkFile: String) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_preview_file)
                dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                dialog.show()
                val imgFile = dialog.findViewById<ImageView>(R.id.img_preview_file)
                Glide.with(requireContext()).load(linkFile).into(imgFile)
                val imgDownload = dialog.findViewById<ImageView>(R.id.img_download_file)
                imgDownload.setOnClickListener {
                    if(hasWriteExternalStoragePermission()){
                        downloadFile(linkFile)
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
        var permissionToRequest = mutableListOf<String>()
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
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}.jpg")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
            manager.enqueue(request)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomSheet() {
        bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_multiple_choice)
        rvPilihData = bottomSheet.findViewById(R.id.rv_pilih_data)
        imgSelesaiPilih = bottomSheet.findViewById(R.id.img_selesai_pilih)
        val imgBatalPilih = bottomSheet.findViewById<ImageView>(R.id.img_batal_pilih)
        tvDataKosong = bottomSheet.findViewById(R.id.tv_data_kosong)
        imgDataKosong = bottomSheet.findViewById(R.id.img_error_sign)

        imgSelesaiPilih?.setOnClickListener {
            val listSelected = adapter.getSelectedItem().filter { it.isChecked }.map { it.title }
            viewModel.insertSelectedTujuan(listSelected)
            bottomSheet.dismiss()
        }

        imgBatalPilih?.setOnClickListener {
            bottomSheet.cancel()
        }

        bottomSheet.setOnCancelListener {
            adapter.resetSelected()
        }

    }

    private fun showBottomSheet() {
        viewModel.listPegawai.observe(viewLifecycleOwner) { list ->
            adapter = RvPilihDataAdapter(list?.filter { !it.isChecked } ?: listOf())
            rvPilihData?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvPilihData?.adapter = adapter

            tvDataKosong?.visibility =
                if (list!!.none { !it.isChecked }) View.VISIBLE else View.GONE
            imgDataKosong?.visibility = if (list.none { !it.isChecked }) View.VISIBLE else View.GONE
        }
        bottomSheet.show()
    }
}