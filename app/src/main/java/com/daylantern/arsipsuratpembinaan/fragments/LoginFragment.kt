package com.daylantern.arsipsuratpembinaan.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.*
import com.daylantern.arsipsuratpembinaan.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sharedPref: SharedPreferences

    lateinit var navCtrl: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navCtrl = Navigation.findNavController(view)
        var isLogin = sharedPref.getBoolean("login", false)

        if(isLogin){
            navCtrl.navigate(R.id.action_loginFragment_to_menu_suratMasuk)
        }

        binding.btnLogin.setOnClickListener {
            val nuptk = binding.inputNuptkLogin.text.toString()
            val password = binding.inputPasswordLogin.text.toString()
            if(nuptk == ""){
                binding.inputNuptkLogin.error = "Email anda masih kosong"
                return@setOnClickListener
            }
//            if(!Constants.isEmailValid(email)){
//                binding.inputEmailLogin.error = "Email anda tidak valid"
//                return@setOnClickListener
//            }
            if(password == ""){
                binding.inputPasswordLogin.error = "Password anda masih kosong"
                return@setOnClickListener
            }
            login(view, nuptk, password)
        }

    }

    private fun login(view: View, nuptk: String, password: String) {
//        apiService.login(nuptk, password).enqueue(object :
//            Callback<ResultResponse> {
//            override fun onResponse(call: Call<ResultResponse>, response: Response<ResultResponse>) {
//                val rbody = response.body()
//                if(rbody?.status == 200){
//                    var editor = sharedPref.edit()
//                    editor
//                        .putBoolean("login", true)
//                        .putInt("idPegawai", rbody.data!!.id_pegawai)
//                        .apply()
//                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_menu_suratMasuk)
//                }else {
//                    Toast.makeText(requireContext(), rbody?.messages, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
//                Log.d("LOGIN", "LOGIN ERROR: ${t.message}")
//            }
//        })
    }
}