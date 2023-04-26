package com.daylantern.arsipsuratpembinaan.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Button
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
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.OnItemClickListener
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.adapters.RvPilihDataAdapter
import com.daylantern.arsipsuratpembinaan.databinding.FragmentTambahDisposisiBinding
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk
import com.daylantern.arsipsuratpembinaan.models.PilihData
import com.daylantern.arsipsuratpembinaan.viewmodels.TambahDisposisiViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

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

    private var listChip: MutableList<Chip> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTambahDisposisiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navC = Navigation.findNavController(view)

        viewModel.fetchSuratMasuk(args.idSuratMasuk)
        viewModel.fetchPegawai()
        setupBottomSheet()

        binding.btnPilihTujuanDisposisi.setOnClickListener {
            showBottomSheet()
        }

        binding.btnMelakukanDisposisi.setOnClickListener {
            saveDisposisi()
        }

        viewModel.suratMasuk.observe(viewLifecycleOwner) {
            if (it != null) {
                suratMasuk = it
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

        viewModel.isSuccess.observe(viewLifecycleOwner) {success ->
            if(success){
                Toast.makeText(requireContext(), viewModel.message.value, Toast.LENGTH_SHORT).show()
                navC.navigate(R.id.menu_suratMasuk)
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
        val pihakTujuan = listIdPegawai.joinToString(", ")
        Log.d("LIST PIHAK TUJUAN", pihakTujuan)
        viewModel.saveDisposisi(
            suratMasuk?.idSuratMasuk!!,
            suratMasuk?.idDisposisi!!,
            binding.inputCatatanDisposisi.text?.trim().toString(),
            pihakTujuan
        )
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