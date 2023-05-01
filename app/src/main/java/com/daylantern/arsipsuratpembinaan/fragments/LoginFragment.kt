package com.daylantern.arsipsuratpembinaan.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.*
import com.daylantern.arsipsuratpembinaan.databinding.FragmentLoginBinding
import com.daylantern.arsipsuratpembinaan.viewmodels.LoginViewModel
import com.daylantern.arsipsuratpembinaan.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sharedPref: SharedPreferences

    lateinit var navC: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Login"
        navC = Navigation.findNavController(view)
        var isLogin = sharedPref.getBoolean("login", false)

        if(isLogin){
            navC.navigate(R.id.action_loginFragment_to_menu_suratMasuk)
        }
        observeIsLoginSuccess()

        binding.btnLogin.setOnClickListener {
            val nuptk = binding.inputNuptkLogin.text.toString()
            val password = binding.inputPasswordLogin.text.toString()
            if(nuptk == ""){
                binding.inputNuptkLogin.error = "Email anda masih kosong"
                return@setOnClickListener
            }
            if(password == ""){
                binding.inputPasswordLogin.error = "Password anda masih kosong"
                return@setOnClickListener
            }
            viewModel.login(nuptk, password)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                Log.d("error", it)
//                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeIsLoginSuccess(){
        viewModel.isSuccess.observe(viewLifecycleOwner){isSuccess ->
            if(isSuccess){
                navC.navigate(R.id.action_loginFragment_to_menu_suratMasuk)
            }else{
                Toast.makeText(requireContext(), "Login gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }
}