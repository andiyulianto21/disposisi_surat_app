package com.daylantern.suratatmaluhur.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.suratatmaluhur.*
import com.daylantern.suratatmaluhur.Constants.isEmailValid
import com.daylantern.suratatmaluhur.Constants.textChangedListener
import com.daylantern.suratatmaluhur.Constants.toastSuccess
import com.daylantern.suratatmaluhur.Constants.toastWarning
import com.daylantern.suratatmaluhur.databinding.FragmentLoginBinding
import com.daylantern.suratatmaluhur.viewmodels.LoginViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
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

        (activity as AppCompatActivity).supportActionBar?.hide()
        navC = Navigation.findNavController(view)
        
        observeLoading()
        observePegawai()
        observeErrorMessage()
//        observeVerifTokenMessage()
        
        binding.apply {
            textChangedListener(tilEmail)
            textChangedListener(tilPasswordLogin)
    
            tvLupaPassword.setOnClickListener {
                navC.navigate(R.id.action_loginFragment_to_lupaPasswordFragment)
            }
            
            btnLogin.setOnClickListener {
                val email = inputEmail.text.toString()
                val password = inputPasswordLogin.text.toString()
                if(email == ""){
                    tilEmail.error = "Email kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(password == "" || password.length < 6){
                    tilPasswordLogin.error = "Password minimal 6 karakter"
                    return@setOnClickListener
                }
                if(!isEmailValid(email)){
                    tilEmail.error = "Email tidak valid"
                    return@setOnClickListener
                }
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    viewModel.login(email, password, it)
                }
            }
        }
    }
    
    private fun observeErrorMessage(){
        viewModel.errorMessage.observe(viewLifecycleOwner) {response ->
            if(response?.status != 200){
                response?.message?.let { toastWarning(requireContext(), it) }
            }
        }
    }
    
    private fun observePegawai() {
        viewModel.pegawai.observe(viewLifecycleOwner){result ->
            if(result == null)
                return@observe
            if(result.status == 200){
                val data = result.data
                result.message?.let { toastSuccess(requireContext(), it) }
                navC.navigate(R.id.action_loginFragment_to_profileFragment)
                sharedPref.edit()
                    .putBoolean(Constants.PREF_IS_LOGIN, true)
                    .putString(Constants.PREF_LEVEL_AKSES, data.levelAkses)
                    .putInt(Constants.PREF_ID_PEGAWAI, data.idPegawai)
                    .apply()
            }else {
                result.message?.let { toastWarning(requireContext(), it) }
            }
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner){isLoading ->
            if(isLoading == null)
                return@observe
            binding.pbLoading.visibility = if(isLoading) View.VISIBLE else View.GONE
        }
    }
}