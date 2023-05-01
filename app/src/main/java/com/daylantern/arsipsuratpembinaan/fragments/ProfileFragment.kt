package com.daylantern.arsipsuratpembinaan.fragments

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.databinding.FragmentProfileBinding
import com.daylantern.arsipsuratpembinaan.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var navC: NavController
    lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navC = Navigation.findNavController(view)

//        getDataPegawai()
        viewModel.fetchPegawaiLoggedIn(sharedPref.getInt(Constants.PREF_ID_PEGAWAI, 0))
        observePegawai()

        binding.btnUbahDataDiri.setOnClickListener {
            navC.navigate(R.id.action_menu_profil_to_ubahDataDiriFragment)
        }

        binding.btnUbahPassword.setOnClickListener {
            navC.navigate(R.id.action_menu_profil_to_gantiPasswordFragment)
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Apakah anda yakin ingin logout dari akun ini?")
                .setNegativeButton("Batal") { p0, _ ->
                    p0.cancel()
                }
                .setPositiveButton("Yakin"){ _, _ ->
                    sharedPref.edit().clear().apply()
                    navC.navigate(R.id.action_menu_profil_to_loginFragment)
                }
                .show()
        }
    }

    private fun observePegawai(){
        viewModel.pegawaiLoggedIn.observe(viewLifecycleOwner){
            binding.apply {
                tvNamaPegawai.text = it?.nama
                tvNuptkPegawai.text = it?.nuptk
                tvJabatanPegawai.text = it?.jabatan
            }
        }
    }

    private fun getDataPegawai() {
//        apiService.getDataPegawai(sharedPref.getInt("idPegawai", 0))
//            .enqueue(object : Callback<ResultResponse> {
//                override fun onResponse(
//                    call: Call<ResultResponse>,
//                    response: Response<ResultResponse>
//                ) {
//                    val rbody = response.body()
//                    if(rbody?.status == 200){
//                        binding.tvNamaPegawai.text = rbody.data?.nama
//                        binding.tvJabatanPegawai.text = rbody.data?.jabatan
//                        binding.tvEmailPegawai.text = rbody.data?.email
//                    }else {
//                        sharedPref.edit().clear().apply()
//                        navC.navigate(R.id.action_menu_profil_to_loginFragment)
//                        Toast.makeText(requireContext(), rbody?.messages, Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
//                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
//                }
//            })
    }
}