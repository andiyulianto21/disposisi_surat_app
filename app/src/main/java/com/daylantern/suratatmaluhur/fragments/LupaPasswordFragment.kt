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
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.databinding.FragmentLupaPasswordBinding
import com.daylantern.suratatmaluhur.viewmodels.LupaPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LupaPasswordFragment : Fragment() {
    
    private lateinit var binding: FragmentLupaPasswordBinding
    private lateinit var navC: NavController
    private val viewModel: LupaPasswordViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLupaPasswordBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navC = Navigation.findNavController(view)
        observeLoading()
        observeResult()
        binding.apply {
            Constants.textChangedListener(tilEmail)
            
            btnKirimEmail.setOnClickListener {
                val email = inputEmail.text?.toString()?.trim()
                if(email.isNullOrEmpty()){
                    tilEmail.error = "Email kosong, silahkan isi"
                    return@setOnClickListener
                }
                if(!Constants.isEmailValid(email)){
                    tilEmail.error = "Email tidak valid"
                    return@setOnClickListener
                }
                viewModel.kirimEmail(email)
            }
        }
    }
    
    private fun observeResult() {
        viewModel.result.observe(viewLifecycleOwner){result ->
            if(result?.status == 200){
                viewModel.clear()
                val action = LupaPasswordFragmentDirections.actionLupaPasswordFragmentToResetPasswordFragment(result.data)
                navC.navigate(action)
                result.message?.let { Constants.toastSuccess(requireContext(), it) }
            }else {
                result?.message?.let { Constants.toastWarning(requireContext(), it) }
            }
        }
    }
    
    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner){isLoading ->
            binding.pbLoading.isVisible = isLoading
        }
    }
}