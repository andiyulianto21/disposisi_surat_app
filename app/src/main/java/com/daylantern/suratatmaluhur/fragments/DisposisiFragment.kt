package com.daylantern.suratatmaluhur.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.adapters.RvSuratMasukAdapter
import com.daylantern.suratatmaluhur.databinding.FragmentDisposisiBinding
import com.daylantern.suratatmaluhur.entities.SuratMasuk
import com.daylantern.suratatmaluhur.models.enums.StatusSuratMasuk
import com.daylantern.suratatmaluhur.viewmodels.AntrianDisposisiViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DisposisiFragment : Fragment() {

    private lateinit var binding: FragmentDisposisiBinding
    private lateinit var navC: NavController
    private val viewModel: AntrianDisposisiViewModel by viewModels()
    private var levelAkses: String? = null
    @Inject lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisposisiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        navC = Navigation.findNavController(view)
        levelAkses = sharedPref.getString(Constants.PREF_LEVEL_AKSES, null)
        viewModel.fetchAntrianDisposisi()
        refreshLayout()
        observeLoading()
        observeSuratBelumDisposisi()
    }
    
    private fun refreshLayout() {
        binding.refreshDisposisi.apply {
            setOnRefreshListener {
                isRefreshing = false
                viewModel.fetchAntrianDisposisi()
            }
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner){isLoading ->
            binding.apply {
                if(isLoading){
                    pbLoading.visibility = View.VISIBLE
                    rvDisposisi.visibility = View.INVISIBLE
                }else {
                    pbLoading.visibility = View.INVISIBLE
                    rvDisposisi.visibility = View.VISIBLE
                }
            }
        }
    }
    
    private fun observeSuratBelumDisposisi() {
        viewModel.antrianDisposisi.observe(viewLifecycleOwner){list ->
            val adapterRv = RvSuratMasukAdapter(list)
            adapterRv.setOnClickListener {idSurat ->
                when(list.find{ it.idSurat.toInt() == idSurat }?.statusSurat){
                    StatusSuratMasuk.Mengajukan.name ->
                        navC.navigate(
                            DisposisiFragmentDirections.actionMenuDisposisiToTambahDisposisiFragment(
                                idSurat
                            )
                        )
                }
            }
            binding.rvDisposisi.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = adapterRv
            }
        }
    }
    
    private fun setupRvDisposisi(list: List<SuratMasuk>) {
    
    }
}