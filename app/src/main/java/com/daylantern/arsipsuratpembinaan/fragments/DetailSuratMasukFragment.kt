package com.daylantern.arsipsuratpembinaan.fragments

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
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
import com.daylantern.arsipsuratpembinaan.databinding.LayoutDisposisiPdfBinding
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk
import com.daylantern.arsipsuratpembinaan.viewmodels.DetailSuratMasukViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class DetailSuratMasukFragment : Fragment() {
    
    private lateinit var binding: FragmentDetailSuratMasukBinding
    private val viewModel: DetailSuratMasukViewModel by viewModels()
    private val args: TambahDisposisiFragmentArgs by navArgs()
    private lateinit var adapterFile: RvFileSuratAdapter
    private lateinit var navC: NavController
    private lateinit var document: PdfDocument
    
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
        
        binding.btnCetakDisposisi.setOnClickListener {
//            generatePdfDisposisi(viewModel.dataSuratMasuk.value?.noSuratMasuk)
            generatePdfFromLayout(viewModel.dataSuratMasuk.value)
        }
    }
    
    private fun generatePdfFromLayout(suratMasuk: SuratMasuk?) {
        val pdfView = LayoutDisposisiPdfBinding.inflate(LayoutInflater.from(requireContext()))
        pdfView.apply {
            tvNoSuratPdf.text = suratMasuk?.noSuratMasuk
            tvTglSuratPdf.text = suratMasuk?.tglSuratMasuk?.let { Constants.showDate(it, true, false) }
            tvInstansiPengirimPdf.text = suratMasuk?.instansiPengirim
            tvTglDiterimaPdf.text = suratMasuk?.tglSuratDiterima?.let { Constants.showDate(it, false, false) }
            tvPerihalPdf.text = suratMasuk?.perihal
            tvTglDisposisiPdf.text = suratMasuk?.tglDisposisi?.let { Constants.showDate(it, false, false) }
            tvCatatanPdf.text = suratMasuk?.catatanDisposisi
            tvTujuanPdf.text = suratMasuk?.disposisiTujuan?.joinToString("\n")
        }
        pdfView.root.measure(
            // Measure to A4 width
            View.MeasureSpec.makeMeasureSpec(
                595, View.MeasureSpec.EXACTLY
            ),
            // Measure to A4 height
            View.MeasureSpec.makeMeasureSpec(
                842, View.MeasureSpec.EXACTLY
            )
        )
        pdfView.root.layout(0,0, pdfView.root.measuredWidth, pdfView.root.measuredHeight)
        PdfGenerator.Builder()
            .setContext(requireActivity())
            .fromViewSource()
            .fromView(pdfView.root)
            .setFileName("disposisi_${suratMasuk?.idSuratMasuk}")
            .setFolderNameOrPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path)
            .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
            .build(object : PdfGeneratorListener() {
                override fun onStartPDFGeneration() {
                
                }
    
                override fun onFinishPDFGeneration() {
                }
    
                override fun onFailure(failureResponse: FailureResponse?) {
                    super.onFailure(failureResponse)
                    Log.d("PDF", "onSuccess: ${failureResponse?.errorMessage}")
                    Toast.makeText(requireContext(), "PDF Failed", Toast.LENGTH_SHORT).show()
                }
    
                override fun onSuccess(response: SuccessResponse?) {
                    super.onSuccess(response)
                    Toast.makeText(requireContext(), "PDF Success", Toast.LENGTH_SHORT).show()
                    Log.d("PDF", "onSuccess: ${response?.path}")
                }
            })
    }
    
    private fun generatePdfDisposisi(noSuratMasuk: String?) {
        document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        
        val canvas = page.canvas
        val paintText = Paint()
        paintText.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL)
        paintText.textSize = 18F
        paintText.color = ContextCompat.getColor(requireContext(), R.color.black)
        paintText.textAlign = Paint.Align.CENTER
        canvas.drawText("Lembar Disposisi", 200F, 25F, paintText)
        paintText.textSize = 14F
        paintText.color = ContextCompat.getColor(requireContext(), R.color.teal_700)
        paintText.textAlign = Paint.Align.LEFT
        canvas.drawText(binding.tvNoSurat.text.toString(), 10F, 60F, paintText)
        document.finishPage(page)
        val noSurat = noSuratMasuk?.replace("/", "_")
        try {
            val filePath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "disposisi_$noSurat.pdf"
            )
            document.writeTo(FileOutputStream(filePath))
            openFile(filePath)
            Log.d("success", "generated pdf: ${filePath.absolutePath}")
            Toast.makeText(requireContext(), "PDF file generated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.d("error", "fail generate pdf: ${e.message}")
            Toast.makeText(requireContext(), "Fail to generate PDF file", Toast.LENGTH_LONG).show()
        }
        document.close()
    }
    
    private fun openFile(filePath: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".fileprovider",
            filePath
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }
    
    private fun observeSuratMasuk() {
        viewModel.dataSuratMasuk.observe(viewLifecycleOwner) { data ->
            if(data!= null){
                setupRvFile(data)
                binding.apply {
                    tvSifatSurat.text = data.sifatSurat
                    tvNoSurat.text = data.noSuratMasuk
                    tvInstansiPengirim.text = data.instansiPengirim
                    tvPerihal.text = data.perihal
                    tvLampiran.text = if(data.lampiran == "0") "-" else data.lampiran
                    tvCatatanDisposisi.text = data.catatanDisposisi
                    tvTglSuratMasukDibuat.text =Constants.showDate(data.tglSuratMasuk, true)
                    tvTglSuratMasukDiterima.text = Constants.showDate(data.tglSuratDiterima, false)
                    if (data.statusSurat == "TERDISPOSISI") {
                        llTerdisposisi.visibility = View.VISIBLE
                        imgTerdisposisi.visibility = View.VISIBLE
                        tvTglSuratMasukTerdisposisi.visibility = View.VISIBLE
                        tvTglSuratMasukTerdisposisi.text =
                            data.tglDisposisi?.let { Constants.showDate(it, false) }
            
                        data.disposisiTujuan.forEach {
                            val chip = Chip(requireContext())
                            chip.text = it
                            chip.setChipBackgroundColorResource(R.color.teal_700)
                            chip.setTextColor(resources.getColor(R.color.white))
                            chipDisposisiTertuju.addView(chip)
                        }
                    } else {
                        llTerdisposisi.visibility = View.GONE
                        imgTerdisposisi.visibility = View.GONE
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
                binding.btnCetakDisposisi.visibility = if (isLoad) View.GONE else View.VISIBLE
            } else {
                binding.pbTambahDisposisi.visibility = View.GONE
                binding.scrollTambahDisposisi.visibility = View.GONE
                binding.btnCetakDisposisi.visibility = View.GONE
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
            RvFileSuratAdapter(data.fileSurat.map { it.replace("localhost", Constants.IP_ADDRESS) })
        binding.rvFileSurat.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFileSurat.adapter = adapterFile
        adapterFile.setOnItemClicked(object : RvFileSuratAdapter.OnItemClickListener {
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
                    Toast.makeText(requireContext(), "Downloading", Toast.LENGTH_SHORT).show()
                    
                }
            }
        })
    }
    
    private fun downloadFile(linkFile: String) {
        try {
            val manager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(linkFile)
            val request = DownloadManager.Request(uri)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${System.currentTimeMillis()}.jpg"
            )
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            manager.enqueue(request)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }
}