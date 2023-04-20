package com.daylantern.arsipsuratpembinaan

import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.daylantern.arsipsuratpembinaan.activities.LoginActivity
import com.daylantern.arsipsuratpembinaan.databinding.ActivityMainBinding
import com.daylantern.arsipsuratpembinaan.fragments.LoginFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        setContentView(binding.root)

//        var isLogin = sharedPreferences.getBoolean("login", false)
//        if(!isLogin){
//            startActivity(Intent(applicationContext, LoginActivity::class.java))
//        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val controller = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(controller)

        controller.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.ubahDataDiriFragment
                || destination.id == R.id.gantiPasswordFragment
                || destination.id == R.id.loginFragment
                || destination.id == R.id.tambahSuratMasukFragment) {
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }

    }

    fun login(){

    }
}