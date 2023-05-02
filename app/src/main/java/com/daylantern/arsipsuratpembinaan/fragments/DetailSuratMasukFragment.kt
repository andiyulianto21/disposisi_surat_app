package com.daylantern.arsipsuratpembinaan.fragments

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.adapters.RvFileSuratAdapter
import com.daylantern.arsipsuratpembinaan.databinding.FragmentDetailSuratMasukBinding
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk
import com.daylantern.arsipsuratpembinaan.viewmodels.DetailSuratMasukViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSuratMasukFragment : Fragment() {

    private lateinit var binding: FragmentDetailSuratMasukBinding
    private val viewModel: DetailSuratMasukViewModel by viewModels()
    private val args: TambahDisposisiFragmentArgs by navArgs()
    private lateinit var adapterFile: RvFileSuratAdapter
    private lateinit var navC: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailSuratMasukBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navC = view.findNavController()
        (activity as AppCompatActivity).supportActionBar?.title = "Detail Surat Masuk"
        viewModel.fetchSuratMasuk(args.idSuratMasuk)

        observeLoading()
        observeErrorMessage()
        observeSuratMasuk()
    }

    private fun observeSuratMasuk() {
        viewModel.dataSuratMasuk.observe(viewLifecycleOwner){data ->
            val tglMasuk = Constants.convertDateStringToCalendar(data!!.tglSuratMasuk, true)
            val tglDiterima = Constants.convertDateStringToCalendar(data.tglSuratDiterima, false)
            setupRvFile(data)
            binding.apply {
                tvSifatSurat.text = data.sifatSurat
                tvNoSurat.text = data.noSuratMasuk
                tvInstansiPengirim.text = data.instansiPengirim
                tvPerihal.text = data.perihal
                tvCatatanDisposisi.text = data.catatanDisposisi
                tvTglSuratMasukDibuat.text = Constants.showDate(tglMasuk, true)
                tvTglSuratMasukDiterima.text = Constants.showDate(tglDiterima, false)
                if(data.statusSurat == "TERDISPOSISI"){
                    llTerdisposisi.visibility = View.VISIBLE
                    imgTerdisposisi.visibility = View.VISIBLE
                    tvTglSuratMasukTerdisposisi.visibility = View.VISIBLE
                    val tglDisposisi =
                        Constants.convertDateStringToCalendar(data.tglDisposisi!!, false)
                    tvTglSuratMasukTerdisposisi.text = Constants.showDate(tglDisposisi, false)

                    data.disposisiTujuan.forEach {
                        val chip = Chip(requireContext())
                        chip.text = it
                        chip.setChipBackgroundColorResource(R.color.teal_700)
                        chip.setTextColor(resources.getColor(R.color.white))
                        chipDisposisiTertuju.addView(chip)
                    }
                }else {
                    llTerdisposisi.visibility = View.GONE
                    imgTerdisposisi.visibility = View.GONE
                    tvTglSuratMasukTerdisposisi.visibility = View.GONE
                }
            }
        }
    }

    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoad ->
            if (isLoad != null) {
                binding.pbTambahDisposisi.visibility = if (isLoad) View.VISIBLE else View.GONE
                binding.scrollTambahDisposisi.visibility = if (isLoad) View.GONE else View.VISIBLE
                binding.btnSimpanSuratMasuk.visibility = if (isLoad) View.GONE else View.VISIBLE
            } else {
                binding.pbTambahDisposisi.visibility = View.GONE
                binding.scrollTambahDisposisi.visibility = View.GONE
                binding.btnSimpanSuratMasuk.visibility = View.GONE
            }
        }
    }

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner){message ->
            if(!message.isNullOrEmpty()){
//                binding.scrollTambahDisposisi.visibility = View.GONE
//                binding.btnSimpanSuratMasuk.visibility = View.GONE
                AlertDialog.Builder(requireContext())
                    .setTitle("Terjadi Error")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Kembali") { _, _ ->
                        navC.popBackStack()
                    }
                    .show()
//                binding.tvErrorMessage.text = message
//                binding.imgErrorSign.visibility = View.VISIBLE
//                binding.tvErrorMessage.visibility = View.VISIBLE
            }else {
//                binding.imgErrorSign.visibility = View.GONE
//                binding.tvErrorMessage.visibility = View.GONE
//                binding.scrollTambahDisposisi.visibility = View.VISIBLE
//                binding.btnSimpanSuratMasuk.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRvFile(data: SuratMasuk) {
        adapterFile = RvFileSuratAdapter(data.fileSurat.map { it.replace("localhost", Constants.IP_ADDRESS) })
        binding.rvFileSurat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFileSurat.adapter = adapterFile
        adapterFile.setOnItemClicked(object: RvFileSuratAdapter.OnItemClickListener{
            override fun onImageClicked(linkFile: String) {
                val dialog = Dialog(requireContext())
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
}