package com.daylantern.arsipsuratpembinaan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.arsipsuratpembinaan.adapters.RvSuratMasukAdapter
import com.daylantern.arsipsuratpembinaan.databinding.FragmentDisposisiBinding
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk
import com.daylantern.arsipsuratpembinaan.fragments.SuratMasukFragmentDirections
import com.daylantern.arsipsuratpembinaan.viewmodels.AntrianDisposisiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisposisiFragment : Fragment() {

    private lateinit var binding: FragmentDisposisiBinding
    private lateinit var navC: NavController
    private val viewModel: AntrianDisposisiViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisposisiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        (activity as AppCompatActivity).supportActionBar?.title = "Antrian Disposisi"
        navC = Navigation.findNavController(view)
    
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
            setupRvDisposisi(list)
        }
    }
    
    private fun setupRvDisposisi(list: List<SuratMasuk>) {
        val adapterRv = RvSuratMasukAdapter(list)
        adapterRv.setOnClickListener {position ->
            if (list[position].statusSurat == "MENUNGGU_DISPOSISI") {
                val action =
                    DisposisiFragmentDirections.actionMenuDisposisiToTambahDisposisiFragment(
                        list[position].idSuratMasuk.toInt())
                navC.navigate(action)
            }
        }
        binding.rvDisposisi.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterRv
        }
    }
}