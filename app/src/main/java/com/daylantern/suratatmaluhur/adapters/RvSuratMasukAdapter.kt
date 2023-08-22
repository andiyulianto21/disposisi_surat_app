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
import com.daylantern.suratatmaluhur.entities.SuratMasuk
import com.daylantern.suratatmaluhur.models.enums.StatusSuratMasuk

class RvSuratMasukAdapter(private val list: List<SuratMasuk>) :
    RecyclerView.Adapter<RvSuratMasukAdapter.ViewHolder>() {
    
    private lateinit var context: Context
    private var listener: ((Int) -> Unit)? = null
    fun setOnClickListener(action: (Int) -> Unit) { this.listener = action }
    
    inner class ViewHolder(val binding: CardSuratMasukBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(surat: SuratMasuk, context: Context) {
            binding.apply {
                tvNoSurat.text = surat.noSurat
                tvPerihal.text = surat.perihal
                tvInstansiPengirim.text = surat.instansiPengirim
                tvTglSurat.text = Constants.showDate(surat.tglSurat, true)
                tvStatusSurat.text = surat.statusSurat
                tvSifatDisposisi.isVisible = surat.statusSurat != StatusSuratMasuk.Mengajukan.name
                tvSifatDisposisi.text =
                    if(surat.statusSurat != StatusSuratMasuk.Mengajukan.name) " | ${surat.sifatDisposisi}"
                    else null
                layoutStatusSurat.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(
                    if (surat.statusSurat != StatusSuratMasuk.Mengajukan.name) R.color.green_success
                    else R.color.red_warning
                ))
                itemView.setOnClickListener { listener?.invoke(surat.idSurat.toInt()) }
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