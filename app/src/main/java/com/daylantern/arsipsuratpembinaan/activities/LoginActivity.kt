package com.daylantern.arsipsuratpembinaan.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.daylantern.arsipsuratpembinaan.*
import com.daylantern.arsipsuratpembinaan.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login(){
        val email = binding.inputEmailLogin.editText?.text.toString()
        val password = binding.inputPasswordLogin.editText?.text.toString()

        if(email != "" && password != ""){
//            apiService.login(email, password).enqueue(object :
//                Callback<ResultResponse> {
//                override fun onResponse(call: Call<ResultResponse>, response: Response<ResultResponse>) {
//                    val rbody = response.body()
//                    if(rbody?.status == 200){
////                        Log.d("LOGIN", "200")
//                        var editor = sharedPref.edit()
//                        editor
//                            .putBoolean("login", true)
//                            .putInt("idPegawai", rbody.data!!.id_pegawai)
//                            .putString("namaPegawai", rbody.data.nama)
//                            .putString("jabatan", rbody.data.jabatan)
//                            .putString("email", rbody.data.email)
//                            .apply()
//                        finish()
//                        startActivity(Intent(applicationContext, MainActivity::class.java))
//                        Toast.makeText(applicationContext, "${rbody.data}", Toast.LENGTH_SHORT).show()
//                    }else {
////                        Log.d("LOGIN", "400")
//                        Toast.makeText(applicationContext, rbody?.messages, Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<ResultResponse>, t: Throwable) {
//                    Log.d("LOGIN", "LOGIN ERROR: ${t.message}")
//                }
//
//            })
        }else {
            Log.d("LOGIN", "GAGAL")
            Toast.makeText(applicationContext, "Email/password anda masih kosong!", Toast.LENGTH_SHORT).show()
        }
    }
}