package com.daylantern.arsipsuratpembinaan

import android.location.GnssAntennaInfo.Listener
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.arsipsuratpembinaan.adapters.RvSuratMasukAdapter
import com.daylantern.arsipsuratpembinaan.databinding.FragmentSuratMasukBinding
import com.daylantern.arsipsuratpembinaan.fragments.TambahDisposisiFragmentArgs
import com.daylantern.arsipsuratpembinaan.viewmodels.SuratMasukViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SuratMasukFragment : Fragment() {

    private lateinit var binding: FragmentSuratMasukBinding
    private lateinit var navC: NavController
    private lateinit var adapter: RvSuratMasukAdapter

    private val viewModel: SuratMasukViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuratMasukBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navC = Navigation.findNavController(view)
        viewModel.fetchSuratMasuk()

        viewModel.suratMasuk.observe(viewLifecycleOwner){
            adapter = RvSuratMasukAdapter(it)
            binding.rvSuratMasuk.layoutManager = LinearLayoutManager(requireContext())
            adapter.setOnClickListener {position ->
                if (it[position].statusSurat == "MENUNGGU_DISPOSISI") {
                    val action =
                        SuratMasukFragmentDirections.actionMenuSuratMasukToTambahDisposisiFragment(
                            it[position].idSuratMasuk.toInt())
                    navC.navigate(action)
                } else if (it[position].statusSurat == "TERDISPOSISI") {
                    val action =
                        SuratMasukFragmentDirections.actionMenuSuratMasukToDetailSuratMasukFragment(
                            it[position].idSuratMasuk.toInt()
                        )
                    navC.navigate(action)
                }
//                    Toast.makeText(requireContext(), it[position].perihal, Toast.LENGTH_SHORT).show()
            }
            binding.rvSuratMasuk.adapter = adapter
        }

        binding.fabTambahSurat.setOnClickListener {
            navC.navigate(R.id.action_menu_suratMasuk_to_tambahSuratMasukFragment)
        }
    }
}