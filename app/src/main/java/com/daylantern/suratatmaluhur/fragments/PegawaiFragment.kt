package com.daylantern.suratatmaluhur.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.adapters.RvPegawaiAdapter
import com.daylantern.suratatmaluhur.databinding.BottomSheetPegawaiBinding
import com.daylantern.suratatmaluhur.databinding.FragmentPegawaiBinding
import com.daylantern.suratatmaluhur.entities.Pegawai
import com.daylantern.suratatmaluhur.viewmodels.PegawaiViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PegawaiFragment : Fragment() {
    
    private lateinit var binding: FragmentPegawaiBinding
    private lateinit var navC: NavController
    private val viewModel: PegawaiViewModel by viewModels()
    private lateinit var adapterRv: RvPegawaiAdapter
    private lateinit var bottomSheet: BottomSheetDialog
    private lateinit var arrayAdapterBagian: ArrayAdapter<String>
    @Inject lateinit var sharedPref: SharedPreferences
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPegawaiBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navC = Navigation.findNavController(view)
        bottomSheet = BottomSheetDialog(requireContext())
        viewModel.getPegawai()
        viewModel.getBagian()
        observePegawai()
        observeOperationMessage()
        observeBagian()
        
        binding.apply {
            fabTambahPegawai.setOnClickListener { addPegawai() }
        }
    }
    
    private fun observeBagian() {
        viewModel.bagian.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                arrayAdapterBagian = ArrayAdapter(
                    requireContext(),
                    R.layout.dropdowntext_pegawai, result.data.map { "${it.kodeBagian} - ${it.deskripsi}" }
                )
            }
        }
    }
    
    private fun observeOperationMessage() {
        viewModel.operationMessage.observe(viewLifecycleOwner){ result ->
            if(!viewModel.isToastShown)
                if(result.status == 200){
                    Constants.toastSuccess(requireContext(), result.message)
                    bottomSheet.dismiss()
                    if(sharedPref.getString(Constants.PREF_LEVEL_AKSES, "") != Constants.LEVEL_ADMIN) {
                        navC.popBackStack()
                        Constants.toastNormal(requireContext(), "Tidak bisa akses halaman pegawai, karena level akses anda telah berubah")
                    }
                }else {
                    Constants.toastWarning(requireContext(), result.message)
                }
            viewModel.isToastShown = true
        }
    }
    
    private fun addPegawai() {
        val bottomSheetBinding = BottomSheetPegawaiBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        
        bottomSheetBinding.apply {
            btnTambah.isVisible = true
            btnBatal.isVisible = true
            btnBatal.setOnClickListener { bottomSheet.cancel() }
            Constants.textChangedListener(tilNamaPegawai)
            Constants.textChangedListener(tilEmailPegawai)
            Constants.textChangedListener(tilJabatanPegawai)
            Constants.textChangedListener(tilPasswordPegawai)
//            Constants.textChangedListener(tilKodeBagian)
            Constants.textChangedListener(tilLevelAkses)
            
            val arrayAdapterLevelAkses = ArrayAdapter(
                requireContext(),
                R.layout.dropdowntext_pegawai,
                listOf("Admin", "Pimpinan", "Kepala Bagian", "Pegawai Bagian", "Pengguna")
            )
            optionLevelAkses.setAdapter(arrayAdapterLevelAkses)
            optionKodeBagian.setAdapter(arrayAdapterBagian)
            
            btnTambah.setOnClickListener {
                val nama = inputNamaPegawai.text.toString().trim()
                val email = inputEmailPegawai.text.toString().trim()
                val password = inputPasswordPegawai.text.toString().trim()
                val jabatan = inputJabatanPegawai.text.toString().trim()
                val kodeBagian = tilKodeBagian.editText?.text.toString().trim()
                val levelAkses = tilLevelAkses.editText?.text.toString().trim()
                
                if (nama.isEmpty()) {
                    tilNamaPegawai.error = "Nama masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if (email.isEmpty()) {
                    tilEmailPegawai.error = "Email masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if (!Constants.isEmailValid(email)) {
                    tilEmailPegawai.error = "Email tidak valid"
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    tilPasswordPegawai.error = "Password masih kosong, silahkan isi"
                    return@setOnClickListener
                }
                if (password.length < 6) {
                    tilPasswordPegawai.error = "Password minimal 6 karakter"
                    return@setOnClickListener
                }
                if (jabatan.isEmpty()) {
                    tilJabatanPegawai.error = "Jabatan masih kosong, silahkan isi"
                    return@setOnClickListener
                }
//                if (kodeBagian.isEmpty()) {
//                    tilKodeBagian.error = "Kode bagian masih kosong, silahkan isi"
//                    return@setOnClickListener
//                }
                if (levelAkses.isEmpty()) {
                    tilLevelAkses.error = "Level akses masih kosong, silahkan isi"
                    return@setOnClickListener
                }
    
                Constants.alertDialog(requireContext(), "Tambah Pegawai",
                    "Apakah Anda yakin ingin menambah pegawai ini?",
                    "Batal", { p0, _ ->
                        p0.cancel()
                    },
                    "Tambah", { p0, _ ->
                        val kodeBagianFormatted = kodeBagian.substringBefore("-").trim()
                        viewModel.tambahPegawai(
                            email,
                            password,
                            kodeBagianFormatted,
                            nama,
                            jabatan,
                            levelAkses
                        )
                        p0.dismiss()
                    })
            }
        }
    }
    
    private fun observePegawai() {
        viewModel.pegawai.observe(viewLifecycleOwner) { result ->
            if (result.status == 200) {
                adapterRv = RvPegawaiAdapter(result.data)
                binding.rvPegawai.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = adapterRv
                }
                
                adapterRv.setOnClickListener {
                    detailBottomSheet(it, false)
                }
                
                adapterRv.setOnUbahListener {
                    detailBottomSheet(it, true)
                }
                
                adapterRv.setOnDeleteListener {idPegawai ->
                    Constants.alertDialog(requireContext(), "Hapus Pegawai",
                        "Apakah anda yakin ingin menghapus pegawai ini?",
                        "Batal", { p0, _ ->
                            p0.cancel()
                        },
                        "Hapus", { p0, _ ->
                            viewModel.deletePegawai(idPegawai)
                            p0.dismiss()
                        })
                }
            }
        }
    }
    
    private fun detailBottomSheet(pegawai: Pegawai, isUbah: Boolean) {
        val bottomSheetBinding = BottomSheetPegawaiBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheet.show()
        
        bottomSheetBinding.apply {
            Constants.textChangedListener(tilNamaPegawai)
            Constants.textChangedListener(tilEmailPegawai)
            Constants.textChangedListener(tilJabatanPegawai)
            Constants.textChangedListener(tilPasswordPegawai)
//            Constants.textChangedListener(tilKodeBagian)
            Constants.textChangedListener(tilLevelAkses)
            
            inputEmailPegawai.setText(pegawai.email)
            inputJabatanPegawai.setText(pegawai.jabatan)
            inputNamaPegawai.setText(pegawai.nama)
            optionKodeBagian.setText(pegawai.kodeBagian)
            optionLevelAkses.setText(pegawai.levelAkses)
            tilPasswordPegawai.visibility = View.GONE
            tvLabelPasswordPegawai.visibility = View.GONE
            switchFiturUbah(isUbah, bottomSheetBinding)
            
            if(isUbah){
                tilPasswordPegawai.visibility = View.VISIBLE
                tvLabelPasswordPegawai.visibility = View.VISIBLE
                tilPasswordPegawai.isEnabled = true
                inputPasswordPegawai.hint = "Password baru"
                btnTambah.text = "Ubah Pegawai"
                btnBatal.setOnClickListener { bottomSheet.cancel() }
                inputNamaPegawai.setSelection(pegawai.nama.length)
                inputJabatanPegawai.setSelection(pegawai.jabatan.length)
                inputEmailPegawai.setSelection(pegawai.email.length)
    
                val arrayAdapterLevelAkses = ArrayAdapter(
                    requireContext(),
                    R.layout.dropdowntext_pegawai,
                    listOf("Admin", "Pimpinan", "Kepala Bagian", "Pegawai Bagian", "Pengguna")
                )
                optionLevelAkses.setAdapter(arrayAdapterLevelAkses)
                optionKodeBagian.setAdapter(arrayAdapterBagian)
                
                btnTambah.setOnClickListener {
                    val nama = inputNamaPegawai.text.toString().trim()
                    val email = inputEmailPegawai.text.toString().trim()
                    val jabatan = inputJabatanPegawai.text.toString().trim()
                    val kodeBagian = tilKodeBagian.editText?.text.toString().trim()
                    val levelAkses = tilLevelAkses.editText?.text.toString().trim()
                    val password = inputPasswordPegawai.text.toString().trim()
    
                    if (nama.isEmpty()) {
                        tilNamaPegawai.error = "Nama masih kosong, silahkan isi"
                        return@setOnClickListener
                    }
                    if (email.isEmpty()) {
                        tilEmailPegawai.error = "Email masih kosong, silahkan isi"
                        return@setOnClickListener
                    }
                    if (!Constants.isEmailValid(email)) {
                        tilEmailPegawai.error = "Email tidak valid"
                        return@setOnClickListener
                    }
                    if (jabatan.isEmpty()) {
                        tilJabatanPegawai.error = "Jabatan masih kosong, silahkan isi"
                        return@setOnClickListener
                    }
//                    if (kodeBagian.isEmpty()) {
//                        tilKodeBagian.error = "Kode bagian masih kosong, silahkan isi"
//                        return@setOnClickListener
//                    }
                    if (levelAkses.isEmpty()) {
                        tilLevelAkses.error = "Level akses masih kosong, silahkan isi"
                        return@setOnClickListener
                    }
                    Constants.alertDialog(requireContext(), "Ubah Pegawai",
                        "Apakah anda yakin ingin mengubah pegawai ini?",
                        "Batal", { p0, _ ->
                            p0.cancel()
                        }, "Ubah", { p0, _ ->
                            if (sharedPref.getInt(
                                    Constants.PREF_ID_PEGAWAI,
                                    0
                                ) == pegawai.idPegawai
                            ) {
                                sharedPref.edit().putString(Constants.PREF_LEVEL_AKSES, levelAkses)
                                    .apply()
                            }
                            val kodeBagianFormatted = kodeBagian.substringBefore("-").trim()
                            viewModel.updatePegawai(
                                pegawai.idPegawai,
                                nama,
                                kodeBagianFormatted,
                                email,
                                levelAkses,
                                jabatan,
                                password
                            )
                            p0.dismiss()
                        })
                }
            }
        }
        
    }
    
    private fun switchFiturUbah(isUbah: Boolean, bottomSheetBinding: BottomSheetPegawaiBinding) {
        bottomSheetBinding.apply {
            btnTambah.visibility = if(isUbah) View.VISIBLE else View.GONE
            btnBatal.visibility = if(isUbah) View.VISIBLE else View.GONE
            tilNamaPegawai.isEnabled = isUbah
            tilJabatanPegawai.isEnabled = isUbah
            tilEmailPegawai.isEnabled = isUbah
            tilKodeBagian.isEnabled = isUbah
            tilLevelAkses.isEnabled = isUbah
        }
    }
}