package com.daylantern.suratatmaluhur.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.adapters.RvSuratMasukAdapter
import com.daylantern.suratatmaluhur.databinding.FragmentSuratMasukBinding
import com.daylantern.suratatmaluhur.models.enums.StatusSuratMasuk
import com.daylantern.suratatmaluhur.viewmodels.SuratMasukViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SuratMasukFragment : Fragment() {
    
    private lateinit var binding: FragmentSuratMasukBinding
    private lateinit var navC: NavController
    private lateinit var adapterRv: RvSuratMasukAdapter
    private var levelAkses: String? = null
    private var idPegawai: Int = 0
    private val viewModel: SuratMasukViewModel by viewModels()
    @Inject lateinit var pref: SharedPreferences
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuratMasukBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        navC = Navigation.findNavController(view)
        levelAkses = pref.getString(Constants.PREF_LEVEL_AKSES, null)
        idPegawai = pref.getInt(Constants.PREF_ID_PEGAWAI, 0)
        
        binding.apply {
            fabTambahSurat.isVisible = levelAkses == Constants.LEVEL_ADMIN
            fabTambahSurat.setOnClickListener {
                navC.navigate(R.id.action_menu_suratMasuk_to_tambahSuratMasukFragment)
            }
        }
        viewModel.getSuratMasuk(idPegawai)
        
        refreshLayout()
        observeLoading()
        observeSuratMasuk()
        observeErrorMessage()
    }
    
    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if(!viewModel.isToastShown) {
                Constants.toastWarning(requireContext(), message)
                viewModel.isToastShown = true
            }
        }
    }
    
    private fun refreshLayout() {
        binding.refreshSuratMasuk.apply {
            setOnRefreshListener {
                isRefreshing = false
                viewModel.getSuratMasuk(idPegawai)
            }
        }
    }
    
    private fun observeSuratMasuk() {
        viewModel.suratMasuk.observe(viewLifecycleOwner) { listSurat ->
            adapterRv = RvSuratMasukAdapter(listSurat)
            binding.rvSuratMasuk.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = adapterRv
            }
            adapterRv.setOnClickListener { idSurat ->
                when(listSurat.find{ it.idSurat.toInt() == idSurat }?.statusSurat){
                    StatusSuratMasuk.Mengajukan.name ->
                        navC.navigate(
                            if(levelAkses == Constants.LEVEL_PIMPINAN) SuratMasukFragmentDirections.actionMenuSuratMasukToTambahDisposisiFragment(idSurat)
                            else SuratMasukFragmentDirections.actionMenuSuratMasukToDetailSuratMasukFragment(idSurat))
                    StatusSuratMasuk.Terdisposisi.name ->
                        navC.navigate(SuratMasukFragmentDirections.actionMenuSuratMasukToDetailSuratMasukFragment(idSurat))
                }
            }
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.apply {
                pbSuratMasuk.isVisible = isLoading
                rvSuratMasuk.isVisible = !isLoading
            }
        }
    }
}