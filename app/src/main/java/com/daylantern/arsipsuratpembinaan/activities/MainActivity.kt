package com.daylantern.arsipsuratpembinaan.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val controller = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(controller)

        controller.addOnDestinationChangedListener {_, destination, _ ->
            if(destination.id == R.id.loginFragment){
                (this as AppCompatActivity).supportActionBar?.hide()
            }else {
                (this as AppCompatActivity).supportActionBar?.show()
            }
        }
        
        controller.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.ubahDataDiriFragment
                || destination.id == R.id.gantiPasswordFragment
                || destination.id == R.id.loginFragment
                || destination.id == R.id.tambahSuratMasukFragment
                || destination.id == R.id.detailSuratMasukFragment
                || destination.id == R.id.tambahDisposisiFragment
                || destination.id == R.id.cameraFragment
                || destination.id == R.id.instansiFragment
            ) {
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }

    }
}