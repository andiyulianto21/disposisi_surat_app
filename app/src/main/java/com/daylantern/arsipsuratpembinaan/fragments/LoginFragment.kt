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
import com.google.firebase.messaging.FirebaseMessaging
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

        (activity as AppCompatActivity).supportActionBar?.hide()
        navC = Navigation.findNavController(view)
        val isLogin = sharedPref.getBoolean("login", false)

        if(isLogin){
            navC.navigate(R.id.action_loginFragment_to_menu_suratMasuk)
        }

        binding.btnLogin.setOnClickListener {
            val nuptk = binding.inputNip.text.toString()
            val password = binding.inputPasswordLogin.text.toString()
            if(nuptk == ""){
                binding.inputNip.error = "NIP masih kosong, silahkan isi"
                return@setOnClickListener
            }
            if(password == ""){
                binding.inputPasswordLogin.error = "Password masih kosong, silahkan isi"
                return@setOnClickListener
            }
            
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                viewModel.login(nuptk, password, it)
                
            }
        }
        
        observeLoading()
        observeMessage()
    
        
    }
    
    private fun observeMessage() {
        viewModel.message.observe(viewLifecycleOwner){response ->
            if(response == null)
                return@observe
            if(response.status == 200){
                navC.navigate(R.id.action_loginFragment_to_menu_suratMasuk)
                sharedPref.edit()
                    .putBoolean("login", true)
                    .putString(Constants.PREF_JABATAN, response.data.jabatan)
                    .putInt(Constants.PREF_ID_PEGAWAI, response.data.idPegawai)
                    .apply()
                Constants.toastSuccess(requireContext(), response.messages!!)
            }else {
                Constants.toastWarning(requireContext(), response.messages!!)
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