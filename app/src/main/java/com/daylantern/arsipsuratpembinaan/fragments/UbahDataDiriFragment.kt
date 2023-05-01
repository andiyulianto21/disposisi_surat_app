package com.daylantern.arsipsuratpembinaan.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.databinding.FragmentUbahDataDiriBinding
import com.daylantern.arsipsuratpembinaan.models.PegawaiModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UbahDataDiriFragment : Fragment() {

    private lateinit var binding: FragmentUbahDataDiriBinding
    private var pegawai: PegawaiModel? = null
    private lateinit var navC: NavController

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUbahDataDiriBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Ubah Data Diri"
        navC = Navigation.findNavController(view)
        val idPegawai = sharedPref.getInt("idPegawai", 0)
        val listJabatan = arrayListOf("Tata Usaha", "Guru", "Kepala Sekolah")
        val arrayAdapterJabatan = ArrayAdapter(requireContext(),
            R.layout.dropdowntext_pegawai, listJabatan)
        getDataPegawai(idPegawai)

        binding.autoCompletePegawai.setAdapter(arrayAdapterJabatan)

        binding.apply {
            btnUbah.setOnClickListener {
                if(inputNamaPegawai.text.toString() == ""){
                    inputNamaPegawai.error = "Nama Pegawai kosong"
                    return@setOnClickListener
                }
//                if(inputEmailPegawai.text.toString() == ""){
//                    inputEmailPegawai.error = "Email Pegawai kosong"
//                    return@setOnClickListener
//                }
//                if(!isEmailValid(inputEmailPegawai.text.toString())){
//                    inputEmailPegawai.error = "Email Pegawai tidak valid"
//                    return@setOnClickListener
//                }
//                apiService.ubahData(
//                    sharedPref.getInt("idPegawai", 0),
//                    inputNamaPegawai.text.toString().trim(),
////                    inputEmailPegawai.text.toString().trim(),
//                    binding.autoCompletePegawai.text.toString()
//                ).enqueue(object : Callback<ResultResponse>{
//                    override fun onResponse(
//                        call: Call<ResultResponse>,
//                        response: Response<ResultResponse>
//                    ) {
//                        val rbody = response.body()
//                        if(rbody?.status == 200){
//                            Toast.makeText(requireContext(), rbody.messages, Toast.LENGTH_SHORT).show()
//                            navC.popBackStack(R.id.menu_profil, true)
//                        }else {
//                            Toast.makeText(requireContext(), rbody?.messages, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
//                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
//                    }
//                })
            }
        }
    }


    private fun getDataPegawai(id: Int){
//        apiService.getDataPegawai(id).enqueue(object : Callback<ResultResponse>{
//            override fun onResponse(call: Call<ResultResponse>, response: Response<ResultResponse>) {
//                val rbody = response.body()
//                if(rbody?.status == 200){
//                    pegawai = rbody.data
//                    binding.inputNamaPegawai.setText(pegawai?.nama)
//                    binding.inputEmailPegawai.setText(pegawai?.email)
//                    binding.autoCompletePegawai.setText(pegawai?.jabatan, false)
//                    Toast.makeText(requireContext(), pegawai.toString(), Toast.LENGTH_SHORT).show()
//                }else {
//                    Toast.makeText(requireContext(), rbody?.messages, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
//                Log.d("Ubah Data Diri", t.localizedMessage)
//            }
//
//        })
    }
}