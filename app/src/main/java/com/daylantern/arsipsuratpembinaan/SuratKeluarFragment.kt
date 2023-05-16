package com.daylantern.arsipsuratpembinaan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.daylantern.arsipsuratpembinaan.databinding.FragmentSuratKeluarBinding
import com.daylantern.arsipsuratpembinaan.entities.FCMNotification
import com.daylantern.arsipsuratpembinaan.entities.FCMRequest
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class SuratKeluarFragment : Fragment() {
    
    private lateinit var binding: FragmentSuratKeluarBinding
    
    @Inject
    lateinit var apiService: ApiService
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSuratKeluarBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        (activity as AppCompatActivity).supportActionBar?.title = "Surat Keluar"
        
        binding.btnNotif.setOnClickListener {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                Log.d("token", "TOKEN: $it")
            }
//            val notif = FCMNotification(
//                "cwJGFn47RVWhhAqzsDV4Xl:APA91bFgyqCds1YSbZKWVgYHSbVK5PwFm1UusSSfVOFfqN5ePNqbbBkzU3mG-mLo5N2kdZNdV3xP4rXZ2okT0zgNq9uk-LiDudxxMJeqr2wL-1-YIW-JaQgJCuswL-K6wYsyP_oxgwL5",
//                FCMRequest("Test aja", "Ini notifikasi dari user")
//            )
//            apiService.sendNotification(notif)
        }
    }
}