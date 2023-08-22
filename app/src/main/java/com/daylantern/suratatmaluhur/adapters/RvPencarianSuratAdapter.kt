package com.daylantern.suratatmaluhur.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.databinding.CardSuratMasukBinding
import com.daylantern.suratatmaluhur.entities.Surat
import com.daylantern.suratatmaluhur.models.enums.StatusSuratKeluar
import com.daylantern.suratatmaluhur.models.enums.StatusSuratMasuk

class RvPencarianSuratAdapter(private val list: List<Surat>) :
    RecyclerView.Adapter<RvPencarianSuratAdapter.ViewHolder>() {
    
    private lateinit var context: Context
    private var listener: ((Surat) -> Unit)? = null
    fun setOnClickListener(action: (Surat) -> Unit) { this.listener = action }
    
    inner class ViewHolder(val binding: CardSuratMasukBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(surat: Surat, context: Context) {
            binding.apply {
                tvNoSurat.text = surat.noSurat ?: "-"
                tvPerihal.text = surat.perihal
                tvInstansiPengirim.text = surat.instansi
                tvTglSurat.text = Constants.showDate(surat.tglSurat, true)
                tvStatusSurat.text = surat.statusSurat
                tvSifatDisposisi.isVisible = surat.statusSurat != StatusSuratMasuk.Mengajukan.name
                tvSifatDisposisi.text =
                    if(surat.sifatDisposisi == null) null
                    else if(surat.statusSurat != StatusSuratMasuk.Mengajukan.name) " | ${surat.sifatDisposisi}"
                    else null
                layoutStatusSurat.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(
                    if(surat.statusSurat == StatusSuratMasuk.Terdisposisi.name || surat.statusSurat == StatusSuratKeluar.Diterima.name){
                        R.color.green_success
                    }else if(surat.statusSurat == StatusSuratKeluar.Dikoreksi.name) com.uk.tastytoasty.R.color.yellow
                    else R.color.red_warning
                ))
                itemView.setOnClickListener { listener?.invoke(surat) }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = CardSuratMasukBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], context)
    }
    override fun getItemCount(): Int = list.size
}