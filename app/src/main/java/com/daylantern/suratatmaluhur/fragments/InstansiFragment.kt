package com.daylantern.suratatmaluhur.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.adapters.RvInstansiAdapter
import com.daylantern.suratatmaluhur.databinding.BottomSheetInstansiBinding
import com.daylantern.suratatmaluhur.databinding.FragmentInstansiBinding
import com.daylantern.suratatmaluhur.entities.Instansi
import com.daylantern.suratatmaluhur.viewmodels.InstansiViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstansiFragment : Fragment() {
    
    private lateinit var binding: FragmentInstansiBinding
    private lateinit var navC: NavController
    private val viewModel: InstansiViewModel by viewModels()
    private lateinit var adapter: RvInstansiAdapter
    private lateinit var bottomSheet: BottomSheetDialog
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstansiBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bottomSheet = BottomSheetDialog(requireContext())
        navC = Navigation.findNavController(view)
        viewModel.fetchInstansi()
        observeLoading()
        observeInstansi()
        observeOperationMessage()
        observeErrorMessage()
        binding.fabTambahInstansi.setOnClickListener { addInstansi() }
    }
    
    private fun addInstansi() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomSheetInstansiBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        
        bottomSheetBinding.apply {
            btnBatalUbahInstansi.visibility = View.VISIBLE
            inputNamaInstansi.isEnabled = true
            inputAlamatInstansi.isEnabled = true
            btnUbahInstansi.text = "Tambah Instansi"
            btnBatalUbahInstansi.setOnClickListener { bottomSheet.cancel() }
            Constants.textChangedListener(tilNamaInstansi)
            
            btnUbahInstansi.setOnClickListener {
                if (inputNamaInstansi.text?.isEmpty() == true) {
                    tilNamaInstansi.error = "Nama instansi masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                Constants.alertDialog(requireContext(), "Tambah Instansi",
                    "Apakah Anda yakin ingin menambah instansi ini?",
                    "Batal", { p0, _ ->
                        p0.cancel()
                    },
                    "Tambah", { p0, _ ->
                        viewModel.addInstansi(
                            inputNamaInstansi.text?.trim().toString(),
                            inputAlamatInstansi.text?.trim().toString()
                        )
                        bottomSheet.dismiss()
                        p0.dismiss()
                    })
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
            binding.pbLoading.isVisible = isLoading
        }
    }
    
    private fun observeOperationMessage() {
        viewModel.operationMessage.observe(viewLifecycleOwner) { result ->
            if (result?.status == 200) {
                Constants.toastSuccess(requireContext(), result.message)
                bottomSheet.dismiss()
            }
            else Constants.toastWarning(requireContext(), result!!.message)
            viewModel.isToastShown = true
        }
    }
    
    private fun observeInstansi() {
        viewModel.instansi.observe(viewLifecycleOwner) { list ->
            adapter = RvInstansiAdapter(list ?: listOf())
            binding.rvInstansi.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvInstansi.adapter = adapter
            
            adapter.setOnDeleteListener {
                Constants.alertDialog(requireContext(), "Hapus Instansi",
                    "Apakah Anda yakin ingin menghapus instansi ini?",
                    "Batal", { p0, _ ->
                        p0.cancel()
                    },
                    "Hapus", { p0, _ ->
                        viewModel.deleteInstansi(it)
                        p0.dismiss()
                        bottomSheet.dismiss()
                    }
                )
            }
            adapter.setOnClickListener { instansi -> detailBottomSheet(instansi, false) }
            adapter.setOnUbahListener { instansi -> detailBottomSheet(instansi, true) }
        }
    }
    
    private fun detailBottomSheet(instansi: Instansi, isUbah: Boolean) {
        val bottomSheetBinding =
            BottomSheetInstansiBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        
        bottomSheetBinding.apply {
            inputNamaInstansi.setText(instansi.namaInstansi)
            inputAlamatInstansi.setText(instansi.alamatInstansi)
            inputAlamatInstansi.hint = if(isUbah) "Opsional" else "-"
            switchFiturUbah(isUbah, bottomSheetBinding)
            
            if (isUbah) {
                btnUbahInstansi.text = "Ubah Instansi"
                btnBatalUbahInstansi.setOnClickListener { bottomSheet.cancel() }
                inputNamaInstansi.setSelection(instansi.namaInstansi.length)
                instansi.alamatInstansi?.let { inputAlamatInstansi.setSelection(it.length) }
                
                btnUbahInstansi.setOnClickListener {
                    val namaInstansi = inputNamaInstansi.text?.trim().toString()
                    if (namaInstansi.isEmpty()) {
                        tilAlamatInstansi.error = "Nama instansi masih kosong, silahkan isi"
                        return@setOnClickListener
                    }
                    Constants.alertDialog(requireContext(), "Ubah Instansi",
                        "Apakah Anda yakin ingin mengubah instansi ini?",
                        "Batal", { p0, _ ->
                            p0.cancel()
                        },
                        "Ubah", { p0, _ ->
                            viewModel.updateInstansi(
                                instansi.idInstansi,
                                inputNamaInstansi.text?.trim().toString(),
                                inputAlamatInstansi.text?.trim().toString()
                            )
                            p0.dismiss()
                        }
                    )
                }
            }
        }
    }
    private fun switchFiturUbah(isUbah: Boolean, bottomSheetBinding: BottomSheetInstansiBinding) {
        bottomSheetBinding.apply {
            btnUbahInstansi.visibility = if (isUbah) View.VISIBLE else View.GONE
            btnBatalUbahInstansi.visibility = if (isUbah) View.VISIBLE else View.GONE
            tilNamaInstansi.isEnabled = isUbah
            tilAlamatInstansi.isEnabled = isUbah
        }
    }
}