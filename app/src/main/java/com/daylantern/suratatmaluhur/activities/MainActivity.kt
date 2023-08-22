package com.daylantern.suratatmaluhur.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var apiService: ApiService
    @Inject lateinit var sharedPreferences: SharedPreferences
    private var backPressedTime: Long = 0
    private lateinit var navC: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)
    
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navC = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.menu_beranda, R.id.menu_suratKeluar, R.id.menu_suratMasuk, R.id.menu_disposisi, R.id.loginFragment, R.id.splashScreenFragment))
        binding.materialToolbar.setupWithNavController(navC, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navC)

        navC.addOnDestinationChangedListener { _, destination, _ ->
            //toolbar
            if(destination.id == R.id.pencarianFragment
                || destination.id == R.id.loginFragment
                || destination.id == R.id.splashScreenFragment
                || destination.id == R.id.lupaPasswordFragment
                || destination.id == R.id.resetPasswordFragment
            ) binding.materialToolbar.visibility = View.GONE
            else binding.materialToolbar.visibility = View.VISIBLE
            //bottom menu
            if (destination.id == R.id.loginFragment
                || destination.id == R.id.notifikasiFragment
                || destination.id == R.id.tambahSuratMasukFragment
                || destination.id == R.id.detailSuratMasukFragment
                || destination.id == R.id.tambahDisposisiFragment
                || destination.id == R.id.cameraFragment
                || destination.id == R.id.instansiFragment
                || destination.id == R.id.pegawaiFragment
                || destination.id == R.id.pencarianFragment
                || destination.id == R.id.splashScreenFragment
                || destination.id == R.id.tambahSuratKeluarFragment
                || destination.id == R.id.detailSuratKeluarFragment
                || destination.id == R.id.lupaPasswordFragment
                || destination.id == R.id.resetPasswordFragment
                || destination.id == R.id.kategoriFragment
                || destination.id == R.id.bagianFragment
                || destination.id == R.id.editSuratKeluarFragment
            ) binding.bottomNavView.visibility = View.GONE
            else binding.bottomNavView.visibility = View.VISIBLE
        }

//        binding.materialToolbar.setNavigationOnClickListener {
//            when(navC.currentDestination?.id){
//                else -> navC.navigateUp()
//            }
//        }
    
        //back button
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                when(navC.currentDestination?.id){
                    R.id.menu_beranda, R.id.loginFragment -> {
                        val currentTime = System.currentTimeMillis()
                        val delay = 2000
                        if (currentTime - backPressedTime > delay) {
                            Toast.makeText(this@MainActivity, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
                            backPressedTime = currentTime
                        } else finish()
                    }
                    R.id.resetPasswordFragment -> navC.navigate(R.id.loginFragment)
                    else -> navC.navigateUp()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}