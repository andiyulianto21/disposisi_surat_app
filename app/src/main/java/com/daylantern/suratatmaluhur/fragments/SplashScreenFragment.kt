package com.daylantern.suratatmaluhur.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.databinding.FragmentSplashScreenBinding
import com.daylantern.suratatmaluhur.viewmodels.SplashScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment : Fragment() {
    
    private lateinit var binding: FragmentSplashScreenBinding
    val viewModel: SplashScreenViewModel by viewModels()
    @Inject lateinit var sharedPref: SharedPreferences
    lateinit var navC: NavController
    private lateinit var dialog: AlertDialog
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        navC = Navigation.findNavController(view)
        
        if(sharedPref.getBoolean(Constants.PREF_IS_LOGIN, false)){
            navC.navigate(R.id.menu_beranda)
        }else {
            Constants.toastWarning(requireContext(), "Silahkan login")
            navC.navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginFragment())
        }

        binding.btnRefresh.setOnClickListener {
            if(sharedPref.getInt(Constants.PREF_ID_PEGAWAI, 0) != 0)
                viewModel.verifikasiToken(sharedPref.getInt(Constants.PREF_ID_PEGAWAI, 0))
            else
                navC.navigate(R.id.loginFragment)
        }
        observeErrorMessage()
    }
    
    private fun observeErrorMessage() {
        viewModel.message.observe(viewLifecycleOwner){msg ->
            if(!msg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                binding.btnRefresh.visibility = View.VISIBLE
            }
        }
    }
    
}