package com.daylantern.arsipsuratpembinaan.fragments

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.adapters.RvSuratMasukAdapter
import com.daylantern.arsipsuratpembinaan.databinding.FragmentSuratMasukBinding
import com.daylantern.arsipsuratpembinaan.viewmodels.SuratMasukViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SuratMasukFragment : Fragment() {

    private lateinit var binding: FragmentSuratMasukBinding
    private lateinit var navC: NavController
    private lateinit var adapter: RvSuratMasukAdapter

    private val viewModel: SuratMasukViewModel by viewModels()

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuratMasukBinding.inflate(layoutInflater)
        when(pref.getString(Constants.PREF_JABATAN, null)?.lowercase()){
            "tata usaha" -> switchFeatureAddSuratMasuk(true)
            else -> switchFeatureAddSuratMasuk(false)
        }
        return binding.root
    }

    private fun switchFeatureAddSuratMasuk(isTataUsaha: Boolean) {
        binding.fabTambahSurat.visibility = if(isTataUsaha) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Surat Masuk"
        navC = Navigation.findNavController(view)
        viewModel.fetchSuratMasuk()

        observeLoading()
        observeRvSuratMasuk()

        binding.fabTambahSurat.setOnClickListener {
            navC.navigate(R.id.action_menu_suratMasuk_to_tambahSuratMasukFragment)
        }
    }

    private fun observeRvSuratMasuk() {
        viewModel.suratMasuk.observe(viewLifecycleOwner){list ->
            adapter = RvSuratMasukAdapter(list)
            binding.rvSuratMasuk.layoutManager = LinearLayoutManager(requireContext())
            adapter.setOnClickListener {position ->
                if (list[position].statusSurat == "MENUNGGU_DISPOSISI") {
                    val action =
                        SuratMasukFragmentDirections.actionMenuSuratMasukToTambahDisposisiFragment(
                            list[position].idSuratMasuk.toInt())
                    navC.navigate(action)
                } else if (list[position].statusSurat == "TERDISPOSISI") {
                    val action =
                        SuratMasukFragmentDirections.actionMenuSuratMasukToDetailSuratMasukFragment(
                            list[position].idSuratMasuk.toInt()
                        )
                    navC.navigate(action)
                }
            }
            binding.rvSuratMasuk.adapter = adapter
        }
    }


    private fun observeLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner){isLoading ->
            binding.apply {
                if(isLoading){
                    pbSuratMasuk.visibility = View.VISIBLE
                    rvSuratMasuk.visibility = View.INVISIBLE
                }else {
                    pbSuratMasuk.visibility = View.INVISIBLE
                    rvSuratMasuk.visibility = View.VISIBLE
                }
            }
        }
    }
}