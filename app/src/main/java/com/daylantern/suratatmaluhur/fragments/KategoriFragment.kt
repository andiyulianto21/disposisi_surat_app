package com.daylantern.suratatmaluhur.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.adapters.RvKategoriAdapter
import com.daylantern.suratatmaluhur.databinding.BottomSheetKategoriBagianBinding
import com.daylantern.suratatmaluhur.databinding.FragmentKategoriBinding
import com.daylantern.suratatmaluhur.entities.Kategori
import com.daylantern.suratatmaluhur.viewmodels.KategoriViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KategoriFragment : Fragment() {

    private lateinit var binding: FragmentKategoriBinding
    private val viewModel: KategoriViewModel by viewModels()
    private lateinit var adapterRv: RvKategoriAdapter
    private lateinit var bottomSheet: BottomSheetDialog
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKategoriBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheet = BottomSheetDialog(requireContext())
        viewModel.getKategori()
        observeKategori()
        observeOperationMessage()
        
        binding.apply {
            fabTambahKategori.setOnClickListener { bottomSheetTambah() }
        }
    }
    
    private fun bottomSheetTambah() {
        val bottomSheetBinding = BottomSheetKategoriBagianBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        bottomSheetBinding.apply {
            tvLabelKode.text = "Kode Kategori"
            btnAksi.text = "Tambah Kategori"
            Constants.textChangedListener(tilKode)
            Constants.textChangedListener(tilDeskripsi)
            btnBatal.setOnClickListener { bottomSheet.cancel() }
            btnAksi.setOnClickListener {
                val kodeKategori = inputKode.text?.trim().toString()
                val deskripsi = inputDeskripsi.text?.trim().toString()
                if(kodeKategori.isEmpty()){
                    tilKode.error = "Kode kategori masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(deskripsi.isEmpty()){
                    tilDeskripsi.error = "Deskripsi kategori masih kosong, silahkan isi"
                    return@setOnClickListener
                }
    
                Constants.alertDialog(requireContext(),
                    "Tambah Kategori",
                    "Apakah anda yakin ingin menambah data kategori surat ini?",
                    "Batal",
                    { p0, _ -> p0.cancel() },
                    "Tambah",
                    { p0, _ ->
                        viewModel.addKategori(kodeKategori, deskripsi)
                        p0.dismiss()
                    })
            }
        }
    }
    
    private fun observeOperationMessage() {
        viewModel.operationMessage.observe(viewLifecycleOwner){result ->
            if(result!=null)
                if(result.status == 200) {
                    Constants.toastSuccess(requireContext(), result.message)
                    bottomSheet.dismiss()
                }
                else Constants.toastWarning(requireContext(), result.message)
        }
    }
    
    
    private fun observeKategori() {
        viewModel.kategori.observe(viewLifecycleOwner){
            adapterRv = RvKategoriAdapter(it)
            binding.apply {
                rvKategori.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rvKategori.adapter = adapterRv
            }
            
            adapterRv.setOnDeleteListener {kode ->
                Constants.alertDialog(requireContext(),
                    "Hapus Kategori",
                    "Apakah anda yakin ingin menghapus data kategori surat ini?",
                    "Batal",
                    { p0, _ -> p0.cancel() },
                    "Hapus",
                    { p0, _ ->
                        viewModel.deleteKategori(kode)
                        p0.dismiss()
                    })
            }
            
            adapterRv.setOnUbahListener {kategori ->
                bottomSheetUbah(kategori)
            }
        }
    }
    
    private fun bottomSheetUbah(kategori: Kategori) {
        
        val bottomSheetBinding = BottomSheetKategoriBagianBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
    
        bottomSheetBinding.apply {
            tvLabelKode.text = "Kode Kategori"
            btnAksi.text = "Ubah Kategori"
            inputKode.setText(kategori.kodeKategori)
            inputKode.setSelection(kategori.kodeKategori.length)
            inputDeskripsi.setText(kategori.deskripsi)
            inputDeskripsi.setSelection(kategori.deskripsi.length)
            Constants.textChangedListener(tilKode)
            Constants.textChangedListener(tilDeskripsi)
            btnBatal.setOnClickListener { bottomSheet.cancel() }
            btnAksi.setOnClickListener {
                val kodeKategori = inputKode.text?.trim().toString()
                val deskripsi = inputDeskripsi.text?.trim().toString()
                if(kodeKategori.isEmpty()){
                    tilKode.error = "Kode kategori masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(deskripsi.isEmpty()){
                    tilDeskripsi.error = "Deskripsi kategori masih kosong, silahkan isi"
                    return@setOnClickListener
                }
    
                Constants.alertDialog(requireContext(),
                    "Ubah Kategori",
                    "Apakah anda yakin ingin mengubah data kategori surat ini?",
                    "Batal",
                    { p0, _ -> p0.cancel() },
                    "Ubah",
                    { p0, _ ->
                        viewModel.updateKategori(kategori.kodeKategori, kodeKategori, deskripsi)
                        p0.dismiss()
                    })
            }
        }
    }
    
}