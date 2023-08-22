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
import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.adapters.RvSuratKeluarAdapter
import com.daylantern.suratatmaluhur.databinding.FragmentSuratKeluarBinding
import com.daylantern.suratatmaluhur.viewmodels.SuratKeluarViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SuratKeluarFragment : Fragment() {
    
    private lateinit var binding: FragmentSuratKeluarBinding
    private lateinit var navC: NavController
    private val viewModel: SuratKeluarViewModel by viewModels()
    private lateinit var adapterSurat: RvSuratKeluarAdapter
    @Inject lateinit var apiService: ApiService
    @Inject lateinit var sharedPref: SharedPreferences
    private var idPegawai: Int = 0
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuratKeluarBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        navC = Navigation.findNavController(view)
        idPegawai = sharedPref.getInt(Constants.PREF_ID_PEGAWAI, 0)
        val levelAkses = sharedPref.getString(Constants.PREF_LEVEL_AKSES, null)
        viewModel.getSuratKeluar(idPegawai)
        observeLoading()
        observeResult()
        refreshLayout()
        binding.apply {
            fabTambahSuratKeluar.isVisible = levelAkses == Constants.LEVEL_PEGAWAI_BAGIAN || levelAkses == Constants.LEVEL_ADMIN
            fabTambahSuratKeluar.setOnClickListener { navC.navigate(R.id.tambahSuratKeluarFragment) }
        }
    }
    
    private fun observeResult() {
        viewModel.result.observe(viewLifecycleOwner){result ->
            if(result?.status == 200){
                adapterSurat = RvSuratKeluarAdapter(result.data)
                binding.apply {
                    rvSuratKeluar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    rvSuratKeluar.adapter = adapterSurat
                    
                    adapterSurat.setOnClickListener { id ->
                        val action = SuratKeluarFragmentDirections.actionMenuSuratKeluarToDetailSuratKeluarFragment(id)
                        navC.navigate(action)
                    }
                }
            }
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoading.isVisible = isLoading
        }
    }
    
    private fun refreshLayout() {
        binding.refreshLayout.apply {
            setOnRefreshListener {
                isRefreshing = false
                viewModel.getSuratKeluar(idPegawai)
            }
        }
    }
}