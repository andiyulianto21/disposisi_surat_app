package com.daylantern.arsipsuratpembinaan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.databinding.FragmentSuratMasukBinding

class SuratMasukFragment : Fragment() {

    private lateinit var binding: FragmentSuratMasukBinding
    private lateinit var navC: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSuratMasukBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navC = Navigation.findNavController(view)

        binding.fabTambahSurat.setOnClickListener {
            navC.navigate(R.id.action_menu_suratMasuk_to_tambahSuratMasukFragment)
        }
    }
}