package com.daylantern.arsipsuratpembinaan.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.adapters.RvSuratMasukAdapter
import com.daylantern.arsipsuratpembinaan.databinding.BottomSheetFilterSuratMasukBinding
import com.daylantern.arsipsuratpembinaan.databinding.FragmentSuratMasukBinding
import com.daylantern.arsipsuratpembinaan.viewmodels.SuratMasukViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Surat Masuk"
        navC = Navigation.findNavController(view)
        setupMenu()
        viewModel.fetchSuratMasuk()
        
        refreshLayout()
        observeLoading()
        observeSuratMasuk()
        observeErrorMessage()

        binding.fabTambahSurat.setOnClickListener {
            navC.navigate(R.id.action_menu_suratMasuk_to_tambahSuratMasukFragment)
        }
    }
    
    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_surat_masuk, menu)
            }
    
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.menu_search -> Toast.makeText(requireContext(), "SEARCH CLICKED", Toast.LENGTH_SHORT).show()
                    R.id.menu_filter -> {
                        val bottomSheet = BottomSheetDialog(requireContext())
                        val filterBinding = BottomSheetFilterSuratMasukBinding.inflate(LayoutInflater.from(requireContext()))
                        bottomSheet.apply {
                            setContentView(filterBinding.root)
                            show()
                        }
                        filterBinding.apply {
//                            cgTglSurat.setOnCheckedChangeListener { group, checkedId ->
//                                group.check(checkedId)
//                            }
                            cgTglSurat.setOnCheckedStateChangeListener { group, checkedIds ->
                                if(checkedIds.isNotEmpty())
                                    group.check(checkedIds[0])
                            }
                            btnFilter.setOnClickListener {
                                val checkedTglSurat = if(cgTglSurat.checkedChipIds.isEmpty()) null else cgTglSurat.findViewById<Chip>(cgTglSurat.checkedChipId).text.toString()
                                if(checkedTglSurat != null) Toast.makeText(requireContext(), checkedTglSurat, Toast.LENGTH_SHORT).show()
                            }
                            btnReset.setOnClickListener {  }
                        }
                    }
                }
                return true
            }
    
        })
    }
    
    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) {message ->
            if (!message.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Terjadi Error")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Refresh") { _, _ ->
                        navC.popBackStack()
                    }
                    .show()
            }
        }
    }
    
    private fun switchFeatureAddSuratMasuk(isTataUsaha: Boolean) {
        binding.fabTambahSurat.visibility = if(isTataUsaha) View.VISIBLE else View.GONE
    }
    
    private fun refreshLayout() {
        binding.refreshSuratMasuk.apply {
            setOnRefreshListener {
                isRefreshing = false
                viewModel.fetchSuratMasuk()
            }
        }
    }
    
    private fun observeSuratMasuk() {
        viewModel.suratMasuk.observe(viewLifecycleOwner){list ->
            adapter = RvSuratMasukAdapter(list)
            binding.rvSuratMasuk.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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