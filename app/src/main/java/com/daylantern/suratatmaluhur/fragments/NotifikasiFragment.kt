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
import com.daylantern.suratatmaluhur.adapters.RvKotakPesanAdapter
import com.daylantern.suratatmaluhur.databinding.FragmentNotifikasiBinding
import com.daylantern.suratatmaluhur.viewmodels.NotifikasiViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotifikasiFragment : Fragment() {
    private lateinit var binding: FragmentNotifikasiBinding
    private val viewModel: NotifikasiViewModel by viewModels()
    private lateinit var adapterKotakPesan: RvKotakPesanAdapter
    private lateinit var navC: NavController
    @Inject lateinit var sharedPref: SharedPreferences
    private var idPegawai = 0
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifikasiBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        navC = Navigation.findNavController(view)
        
        idPegawai = sharedPref.getInt(Constants.PREF_ID_PEGAWAI, 0)
        if(idPegawai != 0){
            viewModel.getNotifikasi(idPegawai)
        }
    
        observeLoading()
        observeNotifikasi()
        observeErrorMessage()
        observeDeleteResult()
    }
    
    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) {msg ->
            if(msg == null) return@observe
            Constants.toastWarning(requireContext(), msg)
        }
    }
    
    private fun observeDeleteResult(){
        viewModel.deleteResult.observe(viewLifecycleOwner){ result ->
            if(result.status == 200) Constants.toastSuccess(requireContext(), result.message)
            else Constants.toastWarning(requireContext(), result.message)
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) {isLoading ->
            binding.pbLoading.visibility = if(isLoading) View.VISIBLE else View.GONE
        }
    }
    
    private fun observeNotifikasi() {
        viewModel.notifikasi.observe(viewLifecycleOwner) {list ->
            if(list != null){
                adapterKotakPesan = RvKotakPesanAdapter(list)
                binding.rvKotakPesan.apply {
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = adapterKotakPesan
                }
                
                adapterKotakPesan.setOnClickListener {notifikasi ->
                    if(notifikasi.statusDibaca == "Belum") viewModel.notifikasiDibaca(notifikasi.idNotifikasi)
                    if (notifikasi.jenisSurat == "Masuk") {
                        val action =
                            if(notifikasi.statusSurat == "Mengajukan" && sharedPref.getString(Constants.PREF_LEVEL_AKSES, null) == "Pimpinan")
                                NotifikasiFragmentDirections.actionNotifikasiFragmentToTambahDisposisiFragment(notifikasi.idSurat)
                            else NotifikasiFragmentDirections.actionNotifikasiFragmentToDetailSuratMasukFragment(notifikasi.idSurat)
                        navC.navigate(action)
                    } else {
                        //surat keluar
                        val action = NotifikasiFragmentDirections.actionNotifikasiFragmentToDetailSuratKeluarFragment(notifikasi.idSurat)
                        navC.navigate(action)
                    }
                }
                
                adapterKotakPesan.setOnDeleteListener { idNotifikasi ->
                    Constants.alertDialog(requireContext(), "Hapus Notifikasi", "Anda yakin ingin menghapus notifikasi ini?",
                        "Batal", {p0,_ -> p0.cancel()},
                        "Hapus", {p0,_ ->
                            viewModel.deleteNotifikasi(idNotifikasi)
                            p0.dismiss()
                            viewModel.getNotifikasi(idPegawai)
                        })
                }
            }
        }
    }
}