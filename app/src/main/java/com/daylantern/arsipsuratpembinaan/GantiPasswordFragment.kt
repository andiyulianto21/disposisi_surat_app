package com.daylantern.arsipsuratpembinaan

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.databinding.FragmentGantiPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class GantiPasswordFragment : Fragment() {

    private lateinit var binding: FragmentGantiPasswordBinding

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var isPasswordLamaValid: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGantiPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVerifikasiPasswordLama.setOnClickListener {
            binding.apply {
                if(inputPasswordLama.text.toString() == ""){
                    inputPasswordLama.error = "Password lama masih kosong"
                }
                else {
//                    apiService.ubahPassword(sharedPref.getInt("idPegawai", 0), 0, inputPasswordLama.text.toString(), null).enqueue(object : Callback<ResultResponse>{
//                        override fun onResponse(
//                            call: Call<ResultResponse>,
//                            response: Response<ResultResponse>
//                        ) {
//                            val rbody = response.body()
//                            if(rbody?.status == 200 && rbody.messages.contains("valid", true)){
//                                isPasswordLamaValid = true
//                                passwordLamaSudahValid()
//                            }else {
//                                Toast.makeText(requireContext(), rbody?.messages, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//
//                        override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
//                            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
//                        }
//
//                    })
                }
            }
        }

        binding.btnGantiPassword.setOnClickListener {
            binding.apply {
                if(inputPasswordBaru.text.toString() == "") {
                    inputPasswordBaru.error = "Password baru masih kosong"
                    return@setOnClickListener
                }
                if(inputKonfirmasiPassword.text.toString() == "") {
                    inputKonfirmasiPassword.error = "Konfirmasi Password baru masih kosong"
                    return@setOnClickListener
                }
                if(inputPasswordBaru.text.toString() != inputKonfirmasiPassword.text.toString()) {
                    Toast.makeText(requireContext(), "Konfirmasi password dengan password baru tidak sesuai", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
//                apiService.ubahPassword(sharedPref.getInt("idPegawai", 0), 1, inputPasswordLama.text.toString(), inputKonfirmasiPassword.text.toString())
//                    .enqueue(object : Callback<ResultResponse>{
//                        override fun onResponse(
//                            call: Call<ResultResponse>,
//                            response: Response<ResultResponse>
//                        ) {
//                            val rbody = response.body()
//                            if(rbody?.status == 200){
//                                Toast.makeText(requireContext(), rbody.messages, Toast.LENGTH_SHORT).show()
//                                Navigation.findNavController(it).popBackStack()
//                            }else {
//                                Toast.makeText(requireContext(), rbody?.messages, Toast.LENGTH_SHORT).show()
//                                inputPasswordBaru.setText("")
//                                inputKonfirmasiPassword.setText("")
//                            }
//                        }
//
//                        override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
//                            Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
//                        }
//
//                    })
            }
        }
    }

    private fun passwordLamaSudahValid() {
        binding.apply {
            tilPasswordLama.isEnabled = false
            btnVerifikasiPasswordLama.isEnabled = false

            tilPasswordBaru.isEnabled = true
            tilKonfirmasiPasswordBaru.isEnabled = true
            btnGantiPassword.isEnabled = true
        }
    }
}