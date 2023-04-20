package com.daylantern.arsipsuratpembinaan.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.adapters.RvPilihDataAdapter
import com.daylantern.arsipsuratpembinaan.databinding.FragmentTambahSuratMasukBinding
import com.daylantern.arsipsuratpembinaan.models.PilihData
import com.daylantern.arsipsuratpembinaan.viewmodels.TambahSuratMasukViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TambahSuratMasukFragment : Fragment() {

    private lateinit var binding: FragmentTambahSuratMasukBinding
    private lateinit var dialogInstansi: Dialog
    private lateinit var selectedInstansi: MutableList<PilihData>

    //    private lateinit var list: MutableList<PilihData>
    private lateinit var adapter: RvPilihDataAdapter
    private lateinit var sifatArrayAdapter: ArrayAdapter<String>
    private val viewModel: TambahSuratMasukViewModel by viewModels()
    private lateinit var calendar: Calendar
    private lateinit var navC: NavController
    private var message: String? = null

    @Inject
    lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTambahSuratMasukBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navC = Navigation.findNavController(view)
        viewModel.fetchInstansi()
        viewModel.fetchSifat()
        (activity as AppCompatActivity).supportActionBar?.title = "Tambah Surat Masuk"

        calendar = Calendar.getInstance()
        selectedInstansi = mutableListOf()
        dialogInstansi = Dialog(requireContext())

        binding.apply {
            inputTglSurat.setOnClickListener {
                showDatePicker(calendar)
            }
            btnTambahInstansiPengirim.setOnClickListener {
//                if(chipInstansi.size > 0){
//                    showTambahInstansi()
//                    adapter.items = selectedInstansi
//                }
//                showTambahInstansi()
            }
            btnTambahInstansiPengirim.setOnClickListener {
                showBottomSheetDialog("Tambah Instansi Baru")
            }
            btnTambahSifatSurat.setOnClickListener {
                showBottomSheetDialog("Tambah Sifat Surat Baru")
            }
            btnSimpanSuratMasuk.setOnClickListener {
                showConfirmationDialog()
            }
        }

        setDropdownInstansi()
        setDropdownSifat()

        viewModel.message.observe(viewLifecycleOwner) { msg ->
            if (msg != null)
                message = msg
        }

        binding.optionInstansiPengirim.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.changeValueInstansi(selectedItem)
        }

        binding.optionSifatSurat.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.changeValueSifat(selectedItem)
        }
    }

    private fun setDropdownInstansi() {
        viewModel.dataInstansi.observe(viewLifecycleOwner) { list ->
            binding.optionInstansiPengirim.setAdapter(
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { it.namaInstansi })
            )
        }

        viewModel.selectedInstansi.observe(viewLifecycleOwner) { nama ->
            binding.optionInstansiPengirim.setText(nama, false)
        }
    }

    private fun setDropdownSifat() {
        viewModel.dataSifat.observe(viewLifecycleOwner) { list ->
            sifatArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, list.map { it.sifatSurat })
            binding.optionSifatSurat.setAdapter(
                sifatArrayAdapter
            )
        }

        viewModel.selectedSifat.observe(viewLifecycleOwner) { sifat ->
            binding.optionSifatSurat.setText(sifat, false);
        }
    }

    private fun showBottomSheetDialog(message: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_tambah_data)
        val tvTambahData = bottomSheetDialog.findViewById<TextView>(R.id.tv_judul_tambah_data)
        val tvError = bottomSheetDialog.findViewById<TextView>(R.id.tv_error_tambah_data)
        val tilTambahData = bottomSheetDialog.findViewById<TextInputLayout>(R.id.til_tambah_data)
        val inputTambahData =
            bottomSheetDialog.findViewById<TextInputEditText>(R.id.input_tambah_data)
        val imgTambahData = bottomSheetDialog.findViewById<ImageView>(R.id.img_tambah_data)
        val imgError = bottomSheetDialog.findViewById<ImageView>(R.id.img_error_sign)


        tvTambahData?.text = message
        tilTambahData?.requestFocus()
        bottomSheetDialog.show()
        imgTambahData?.setOnClickListener {
            if (message.contains("instansi", true)) {
                viewModel.insertInstansi(inputTambahData?.text?.trim().toString(), "")
            } else {
                viewModel.insertSifat(inputTambahData?.text?.trim().toString())
            }
            viewModel.errorBottomSheet.observe(viewLifecycleOwner) { error ->
                if (error.isNullOrBlank()) {
                    imgError?.visibility = View.GONE
                    tvError?.visibility = View.GONE
                    bottomSheetDialog.dismiss()
                    Log.d("error", "BERHASIL DAN KELUAR")
                } else
                    imgError?.visibility = View.VISIBLE
                tvError?.visibility = View.VISIBLE
                tvError?.text = error
            }
        }
    }

    private fun showConfirmationDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_konfirmasi)
        val imgKonfirmasiTutup = dialog.findViewById<ImageView>(R.id.img_konfirmasi_tutup)
        val btnKonfirmasiSimpan = dialog.findViewById<MaterialButton>(R.id.btn_konfirmasi_simpan)
        val tvDeskripsiKonfirmasi = dialog.findViewById<TextView>(R.id.tv_deskripsi_konfirmasi)
        val tvJudulKonfirmasi = dialog.findViewById<TextView>(R.id.tv_judul_konfirmasi)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        imgKonfirmasiTutup.setOnClickListener {
            dialog.dismiss()
        }
        btnKonfirmasiSimpan.text = "Simpan"
        tvDeskripsiKonfirmasi.text = resources.getText(R.string.desk_konfirmasi_tambah_suratmasuk)
        tvJudulKonfirmasi.text = resources.getText(R.string.judul_konfirmasi_tambah_suratmasuk)

        dialog.show()

        btnKonfirmasiSimpan.setOnClickListener {
            val noSurat = binding.inputNoSurat.text?.trim().toString()
            val perihal = binding.inputPerihal.text?.trim().toString()
            val tglSurat = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

            if (noSurat.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Form No Surat belum diisi / kosong",
                    Toast.LENGTH_LONG
                ).show()
                dialog.cancel()
                return@setOnClickListener
            }
            if (perihal.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Form Perihal belum diisi / kosong",
                    Toast.LENGTH_LONG
                ).show()
                dialog.cancel()
                return@setOnClickListener
            }
            viewModel.insertSurat(
                noSurat,
                tglSurat,
                perihal,
            )
            viewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess == true) {
                    Toast.makeText(requireContext(), viewModel.message.value, Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    navC.navigate(R.id.menu_suratMasuk)
                } else {
                    dialog.cancel()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker(calendar: Calendar) {

        val dpd = DatePickerDialog(
            requireContext(),
            { datePicker, y, mm, dd ->
                binding.inputTglSurat.setText("$dd ${Constants.convertMonth(mm)} $y")
                calendar.set(y, mm, dd)
                datePicker.updateDate(y, mm, dd)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
        dpd.show()

    }


//    private fun showTambahInstansi() {
//
//        dialogInstansi.setContentView(R.layout.dialog_pilih_data)
//        val rv = dialogInstansi.findViewById<RecyclerView>(R.id.rv_pilih_data)
//        val tvJudul = dialogInstansi.findViewById<TextView>(R.id.tv_judul)
//        val btnBatal = dialogInstansi.findViewById<Button>(R.id.btn_batal_pilih_data)
//
//        tvJudul.text = "Pilih Instansi Pengirim"
//        rv.layoutManager = LinearLayoutManager(dialogInstansi.context)
//        adapter.items = list.filter { !it.isChecked }
//        rv.adapter = adapter
//        rv.setHasFixedSize(true)
//
//        btnBatal.setOnClickListener {
//            adapter.items.map { it.isChecked = false }
//            dialogInstansi.cancel()
//        }
//
//        dialogInstansi.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//        dialogInstansi.show()
//
//        val btnPilih = dialogInstansi.findViewById<Button>(R.id.btn_pilih_data)
//        btnPilih.setOnClickListener {
//            val list = adapter.items.toList()
//            list.forEach { instansi ->
//                if (instansi.isChecked) {
//                    Log.d("CHIPS", instansi.toString())
////                    val chip = Chip(binding.chipInstansi.context)
////                    chip.text = instansi.title
////                    chip.isCloseIconVisible = true
////                    chip.setOnCloseIconClickListener {
////                        Log.d("chips", "chip ditekan")
////                        instansi.isChecked = false
////                        binding.chipInstansi.removeView(chip)
////                    }
////                    binding.chipInstansi.addView(chip)
//                }
//            }
//            dialogInstansi.dismiss()
//        }
//    }

}