package com.daylantern.suratatmaluhur.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.adapters.RvPencarianSuratAdapter
import com.daylantern.suratatmaluhur.databinding.FragmentPencarianBinding
import com.daylantern.suratatmaluhur.models.enums.StatusSuratMasuk
import com.daylantern.suratatmaluhur.viewmodels.CariSuratViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PencarianFragment : Fragment() {
    
    private lateinit var binding: FragmentPencarianBinding
    private lateinit var navC: NavController
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var adapterRv: RvPencarianSuratAdapter
    private val viewModel: CariSuratViewModel by viewModels()
    @Inject lateinit var sharedPref: SharedPreferences
    private var idPegawai = 0
    private var levelAkses: String? = null
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPencarianBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navC = Navigation.findNavController(view)
        val jenisSurat = listOf("masuk", "keluar")
        var selectedJenisSurat = 0
        
        levelAkses = sharedPref.getString(Constants.PREF_LEVEL_AKSES, null)
        idPegawai = sharedPref.getInt(Constants.PREF_ID_PEGAWAI, 0)
        binding.apply {
            tvHasilCari.text = viewModel.tvHasilCari
            imgBack.setOnClickListener { navC.popBackStack() }
            imgReset.setOnClickListener { pencarian.setText("") }
            pencarian.requestFocus()
            inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.pencarian, 0)
            
            pencarian.setOnKeyListener { _, keyCode, keyEvent ->
                val keyword = pencarian.text?.trim().toString()
                if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    AlertDialog.Builder(requireContext())
                        .setTitle("Pilih Surat")
                        .setSingleChoiceItems(jenisSurat.toTypedArray(), selectedJenisSurat) {_, which ->
                            selectedJenisSurat = which
                        }
                        .setNeutralButton("Batal") { p0, _ -> p0.cancel() }
                        .setPositiveButton("Cari"){p0, _ ->
                            if(keyword.isEmpty()){
                                Constants.toastWarning(requireContext(), "Keyword kosong, silahkan isi terlebih dahulu")
                            }else {
                                if(jenisSurat[selectedJenisSurat] == "masuk"){
                                    tvHasilCari.text = "Hasil pencarian \"$keyword\" di surat masuk"
                                    viewModel.cariSurat(keyword, idPegawai, "masuk")
                                }else {
                                    tvHasilCari.text = "Hasil pencarian \"$keyword\" di surat keluar"
                                    viewModel.cariSurat(keyword, idPegawai, "keluar")
                                }
                                viewModel.tvHasilCari = tvHasilCari.text.toString()
                                p0.dismiss()
                                pencarian.clearFocus()
                                pencarian.text?.clear()
                                viewModel.tvHasilCari = ""
                            }
                        }
                        .show()
                    true
                } else false
            }
        }
        observeResultSurat()
    }
    
    private fun observeResultSurat() {
        viewModel.resultSurat.observe(viewLifecycleOwner){result ->
            if(result == null)return@observe
            if(result.data.isEmpty()){
                Constants.toastNormal(requireContext(), "Data surat tidak ditemukan")
            }
            adapterRv = RvPencarianSuratAdapter(result.data)
            binding.apply {
                rvSurat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rvSurat.adapter = adapterRv
            }
            adapterRv.setOnClickListener {surat ->
                if(surat.statusSurat == StatusSuratMasuk.Terdisposisi.name){
                    navC.navigate(PencarianFragmentDirections.actionPencarianFragmentToDetailSuratMasukFragment(surat.idSurat.toInt()))
                    viewModel.clear()
                }
                else if(surat.statusSurat == StatusSuratMasuk.Mengajukan.name){
                    if(levelAkses == Constants.LEVEL_PIMPINAN) {
                        navC.navigate(PencarianFragmentDirections.actionPencarianFragmentToTambahDisposisiFragment(surat.idSurat.toInt()))
                        viewModel.clear()
                    }
                    else {
                        navC.navigate(PencarianFragmentDirections.actionPencarianFragmentToDetailSuratMasukFragment(surat.idSurat.toInt()))
                        viewModel.clear()
                    }
                }else {
                    navC.navigate(PencarianFragmentDirections.actionPencarianFragmentToDetailSuratKeluarFragment(surat.idSurat.toInt()))
                    viewModel.clear()
                }
            }
        }
    }
}