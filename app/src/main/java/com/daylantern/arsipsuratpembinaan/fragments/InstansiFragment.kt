package com.daylantern.arsipsuratpembinaan.fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.adapters.RvInstansiAdapter
import com.daylantern.arsipsuratpembinaan.databinding.BottomSheetInstansiBinding
import com.daylantern.arsipsuratpembinaan.databinding.FragmentInstansiBinding
import com.daylantern.arsipsuratpembinaan.viewmodels.InstansiViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.uk.tastytoasty.TastyToasty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstansiFragment : Fragment() {
    
    private lateinit var binding: FragmentInstansiBinding
    private lateinit var navC: NavController
    private val viewModel: InstansiViewModel by viewModels()
    private lateinit var adapter: RvInstansiAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstansiBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        (activity as AppCompatActivity).supportActionBar?.title = "Data Instansi"
        navC = Navigation.findNavController(view)
        
        viewModel.fetchInstansi()
        observeLoading()
        observeInstansi()
        observeOperationMessage()
        observeErrorMessage()
        
        binding.fabTambahInstansi.setOnClickListener {
            addInstansi()
        }
    }
    
    private fun addInstansi() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomSheetInstansiBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        
        bottomSheetBinding.apply {
            btnHapusInstansi.visibility = View.INVISIBLE
            btnBatalUbahInstansi.visibility = View.VISIBLE
            inputNamaInstansi.isEnabled = true
            inputAlamatInstansi.isEnabled = true
            btnUbahInstansi.text = "Tambah"
            btnBatalUbahInstansi.setOnClickListener { bottomSheet.cancel() }
            btnUbahInstansi.setOnClickListener {
                
                if (inputNamaInstansi.text?.isEmpty() == true) {
                    inputNamaInstansi.error = "Nama Instansi tidak boleh kosong!"
                    return@setOnClickListener
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("Instansi")
                    .setIcon(R.drawable.ic_add)
                    .setMessage("Apakah Anda yakin ingin menambah instansi ini?")
                    .setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }
                    .setPositiveButton("Tambah") { dialog, _ ->
                        viewModel.addInstansi(
                            inputNamaInstansi.text?.trim().toString(),
                            inputAlamatInstansi.text?.trim().toString()
                        )
                        bottomSheet.dismiss()
                        dialog.dismiss()
                    }.show()
            }
        }
    }
    
    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Pesan Kesalahan")
                    .setIcon(R.drawable.ic_error)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Kembali") { _, _ ->
                        navC.popBackStack()
                    }
                    .show()
            }
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        }
    }
    
    private fun observeOperationMessage() {
        viewModel.operationMessage.observe(viewLifecycleOwner) { result ->
            if (!result?.messages.isNullOrEmpty())
                TastyToasty.makeText(
                    requireContext(), result?.messages,TastyToasty.LONG,
                    if (result?.status == 200) R.drawable.ic_done else R.drawable.ic_error,
                    if (result?.status == 200) R.color.green_success else R.color.red_warning,
                    R.color.white,false
                ).show()
        }
    }
    
    private fun observeInstansi() {
        viewModel.instansi.observe(viewLifecycleOwner) {
            adapter = RvInstansiAdapter(it ?: listOf())
            binding.rvInstansi.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvInstansi.adapter = adapter
            
            adapter.setOnClickListener { instansi ->
                val bottomSheet = BottomSheetDialog(requireContext())
                val bottomSheetBinding =
                    BottomSheetInstansiBinding.inflate(LayoutInflater.from(requireContext()))
                bottomSheet.setContentView(bottomSheetBinding.root)
                bottomSheet.setOnCancelListener { viewModel.changeModeUbah(false) }
                bottomSheet.show()
                bottomSheetBinding.apply {
                    inputNamaInstansi.setText(instansi.namaInstansi)
                    inputAlamatInstansi.setText(instansi.alamatInstansi)
                    
                    viewModel.isModeUbah.observe(viewLifecycleOwner) { isModeUbah ->
                        btnUbahInstansi.text = if (isModeUbah) "Simpan Perubahan" else "Ubah"
                        btnHapusInstansi.visibility =
                            if (isModeUbah) View.INVISIBLE else View.VISIBLE
                        btnBatalUbahInstansi.visibility =
                            if (isModeUbah) View.VISIBLE else View.GONE
                        inputNamaInstansi.isEnabled = isModeUbah
                        inputAlamatInstansi.isEnabled = isModeUbah
                        
                        if (isModeUbah) {
                            btnBatalUbahInstansi.setOnClickListener {
                                viewModel.changeModeUbah(false)
                                inputAlamatInstansi.setText(instansi.alamatInstansi)
                                inputNamaInstansi.setText(instansi.namaInstansi)
                            }
                            btnUbahInstansi.setOnClickListener {
                                if (inputNamaInstansi.text?.isEmpty() == true) {
                                    inputNamaInstansi.error = "Nama Instansi tidak boleh kosong!"
                                } else if (inputNamaInstansi.text?.toString() == (instansi.namaInstansi)
                                    && inputAlamatInstansi.text?.toString() == (instansi.alamatInstansi)
                                ) {
                                    TastyToasty.makeText(
                                        requireContext(), "Tidak bisa mengubah instansi, karena data belum diubah",TastyToasty.LONG,
                                        R.drawable.ic_error,
                                        R.color.red_warning,R.color.white,false
                                    ).show()
                                } else
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("Instansi")
                                        .setIcon(R.drawable.ic_update)
                                        .setMessage("Apakah Anda yakin ingin mengubah instansi ini?")
                                        .setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }
                                        .setPositiveButton("Ubah") { dialog, _ ->
                                            viewModel.updateInstansi(
                                                instansi.idInstansi.toInt(),
                                                inputNamaInstansi.text?.trim().toString(),
                                                inputAlamatInstansi.text?.trim().toString()
                                            )
                                            bottomSheet.dismiss()
                                            dialog.dismiss()
                                        }.show()
                            }
                        } else {
                            btnUbahInstansi.setOnClickListener {
                                viewModel.changeModeUbah(true)
                            }
                            
                            btnHapusInstansi.setOnClickListener {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Instansi")
                                    .setIcon(R.drawable.ic_remove)
                                    .setMessage("Apakah Anda yakin ingin menghapus instansi ini?")
                                    .setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }
                                    .setPositiveButton("Hapus") { dialog, _ ->
                                        viewModel.deleteInstansi(instansi.idInstansi.toInt())
                                        dialog.dismiss()
                                        bottomSheet.dismiss()
                                    }.show()
                            }
                        }
                        
                    }
                }
            }
        }
    }
}