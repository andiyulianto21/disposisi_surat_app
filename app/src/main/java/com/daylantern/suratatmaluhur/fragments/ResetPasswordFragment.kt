package com.daylantern.suratatmaluhur.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.databinding.FragmentResetPasswordBinding
import com.daylantern.suratatmaluhur.viewmodels.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    
    private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var navC: NavController
    private val viewModel: ResetPasswordViewModel by viewModels()
    private val args: ResetPasswordFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navC = Navigation.findNavController(view)
        
        observeLoading()
        observeResult()
        
        binding.apply {
            Constants.textChangedListener(tilToken)
            Constants.textChangedListener(tilPassword)
            Constants.textChangedListener(tilKonfirmasiPassword)
            
            btnResetPassword.setOnClickListener {
                val token = inputToken.text?.toString()?.trim()
                val passwordBaru = inputPassword.text?.toString()?.trim()
                val passwordKonfirmasi = inputKonfirmasiPassword.text?.toString()?.trim()
    
                if(token.isNullOrEmpty()){
                    tilToken.error = "Token kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(passwordBaru.isNullOrEmpty()){
                    tilPassword.error = "Password kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(passwordKonfirmasi.isNullOrEmpty()){
                    tilKonfirmasiPassword.error = "Konfirmasi password kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(passwordBaru.length < 6){
                    tilPassword.error = "Password minimal 6 karakter"
                    return@setOnClickListener
                }
                if(passwordKonfirmasi != passwordBaru){
                    tilPassword.error = "Password baru dan konfirmasi password tidak cocok"
                    tilKonfirmasiPassword.error = "Password baru dan konfirmasi password tidak cocok"
                    return@setOnClickListener
                }
                
                viewModel.resetPassword(args.email, passwordKonfirmasi, token)
            }
        }
    }
    
    private fun observeResult() {
        viewModel.result.observe(viewLifecycleOwner){result ->
            if(result.status == 200){
                navC.navigate(R.id.loginFragment)
                Constants.toastSuccess(requireContext(), result.message)
            }else {
                Constants.toastWarning(requireContext(), result.message)
            }
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner){isLoading ->
            binding.pbLoading.isVisible = isLoading
        }
    }
    
    
}