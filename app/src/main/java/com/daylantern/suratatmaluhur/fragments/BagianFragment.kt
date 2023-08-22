package com.daylantern.suratatmaluhur.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.adapters.RvBagianAdapter
import com.daylantern.suratatmaluhur.databinding.BottomSheetKategoriBagianBinding
import com.daylantern.suratatmaluhur.databinding.FragmentBagianBinding
import com.daylantern.suratatmaluhur.entities.Bagian
import com.daylantern.suratatmaluhur.viewmodels.BagianViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BagianFragment : Fragment() {
    
    private lateinit var binding: FragmentBagianBinding
    private val viewModel: BagianViewModel by viewModels()
    private lateinit var adapterRv: RvBagianAdapter
    private lateinit var bottomSheet: BottomSheetDialog
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBagianBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheet = BottomSheetDialog(requireContext())
        viewModel.getBagian()
        observeBagian()
        observeOperationMessage()
    
        binding.apply {
            fabTambahBagian.setOnClickListener { bottomSheetTambah() }
        }
    }
    
    private fun bottomSheetTambah() {
        val bottomSheetBinding = BottomSheetKategoriBagianBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        bottomSheetBinding.apply {
            tvLabelKode.text = "Kode Bagian"
            btnAksi.text = "Tambah Bagian"
            Constants.textChangedListener(tilKode)
            Constants.textChangedListener(tilDeskripsi)
            btnBatal.setOnClickListener { bottomSheet.cancel() }
            btnAksi.setOnClickListener {
                val kodeBagian = inputKode.text?.trim().toString()
                val deskripsi = inputDeskripsi.text?.trim().toString()
                if(kodeBagian.isEmpty()){
                    tilKode.error = "Kode bagian masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(deskripsi.isEmpty()){
                    tilDeskripsi.error = "Deskripsi bagian masih kosong, silahkan isi"
                    return@setOnClickListener
                }
    
                Constants.alertDialog(requireContext(),
                    "Tambah Bagian",
                    "Apakah anda yakin ingin menambah data bagian ini?",
                    "Batal",
                    { p0, _ -> p0.cancel() },
                    "Tambah",
                    { p0, _ ->
                        viewModel.addBagian(kodeBagian, deskripsi)
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
    
    private fun observeBagian() {
        viewModel.bagian.observe(viewLifecycleOwner){list ->
            adapterRv = RvBagianAdapter(list)
            binding.apply {
                rvBagian.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rvBagian.adapter = adapterRv
            }
        
            adapterRv.setOnDeleteListener {kode ->
                Constants.alertDialog(requireContext(),
                    "Hapus Bagian",
                    "Apakah anda yakin ingin menghapus data bagian ini?",
                    "Batal",
                    { p0, _ -> p0.cancel() },
                    "Hapus",
                    { p0, _ ->
                        viewModel.deleteBagian(kode)
                        p0.dismiss()
                    })
            }
        
            adapterRv.setOnUbahListener {bagian ->
                bottomSheetUbah(bagian)
            }
        }
    }
    
    private fun bottomSheetUbah(bagian: Bagian) {
        val bottomSheetBinding = BottomSheetKategoriBagianBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        
        bottomSheetBinding.apply {
            tvLabelKode.text = "Kode Bagian"
            btnAksi.text = "Ubah Bagian"
            inputKode.setText(bagian.kodeBagian)
            inputKode.setSelection(bagian.kodeBagian.length)
            inputDeskripsi.setText(bagian.deskripsi)
            inputDeskripsi.setSelection(bagian.deskripsi.length)
            Constants.textChangedListener(tilKode)
            Constants.textChangedListener(tilDeskripsi)
            btnBatal.setOnClickListener { bottomSheet.cancel() }
            btnAksi.setOnClickListener {
                val kodeBagian = inputKode.text?.trim().toString()
                val deskripsi = inputDeskripsi.text?.trim().toString()
                if(kodeBagian.isEmpty()){
                    tilKode.error = "Kode bagian masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(deskripsi.isEmpty()){
                    tilDeskripsi.error = "Deskripsi bagian masih kosong, silahkan isi"
                    return@setOnClickListener
                }
    
                Constants.alertDialog(requireContext(),
                    "Ubah Bagian",
                    "Apakah anda yakin ingin mengubah data bagian ini?",
                    "Batal",
                    { p0, _ -> p0.cancel() },
                    "Ubah",
                    { p0, _ ->
                        viewModel.updateBagian(bagian.kodeBagian, kodeBagian, deskripsi)
                        p0.dismiss()
                    })
            }
        }
    }
}