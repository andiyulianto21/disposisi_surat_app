package com.daylantern.arsipsuratpembinaan.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.databinding.FragmentDetailSuratMasukBinding
import com.daylantern.arsipsuratpembinaan.viewmodels.DetailSuratMasukViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSuratMasukFragment : Fragment() {

    private lateinit var binding: FragmentDetailSuratMasukBinding
    private val viewModel: DetailSuratMasukViewModel by viewModels()
    private val args: TambahDisposisiFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailSuratMasukBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchSuratMasuk(args.idSuratMasuk)

        viewModel.isLoading.observe(viewLifecycleOwner){isLoad->
            if(isLoad){
                binding.pbTambahDisposisi.visibility = View.VISIBLE
                binding.scrollTambahDisposisi.visibility = View.GONE
                binding.btnSimpanSuratMasuk.visibility = View.GONE
            }else {
                binding.pbTambahDisposisi.visibility = View.GONE
                binding.scrollTambahDisposisi.visibility = View.VISIBLE
                binding.btnSimpanSuratMasuk.visibility = View.VISIBLE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){message ->
            if(message != null){
                binding.tvErrorMessage.text = message
                binding.imgErrorSign.visibility = View.VISIBLE
                binding.tvErrorMessage.visibility = View.VISIBLE
                binding.scrollTambahDisposisi.visibility = View.GONE
                binding.btnSimpanSuratMasuk.visibility = View.GONE
            }else {
                binding.imgErrorSign.visibility = View.GONE
                binding.tvErrorMessage.visibility = View.GONE
                binding.scrollTambahDisposisi.visibility = View.VISIBLE
                binding.btnSimpanSuratMasuk.visibility = View.VISIBLE
            }
        }

        viewModel.dataSuratMasuk.observe(viewLifecycleOwner){data ->
            val tglMasuk = Constants.convertDateStringToCalendar(data!!.tglSuratMasuk, true)
            val tglDiterima = Constants.convertDateStringToCalendar(data.tglSuratDiterima, false)

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
                        chip.setChipBackgroundColorResource(R.color.purple_200)
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
}