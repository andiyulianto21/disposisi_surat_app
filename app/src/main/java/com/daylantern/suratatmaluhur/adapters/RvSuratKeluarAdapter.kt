package com.daylantern.suratatmaluhur.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.R
import com.daylantern.suratatmaluhur.databinding.CardSuratMasukBinding
import com.daylantern.suratatmaluhur.entities.SuratKeluar

class RvSuratKeluarAdapter(val list: List<SuratKeluar>) :
    RecyclerView.Adapter<RvSuratKeluarAdapter.RvSuratMasukViewHolder>() {
    
    private lateinit var context: Context
    private var listener: ((Int) -> Unit)? = null
    
    fun setOnClickListener(action: (Int) -> Unit) {
        this.listener = action
    }
    
    inner class RvSuratMasukViewHolder(val binding: CardSuratMasukBinding) : ViewHolder(binding.root) {
        fun bind(data: SuratKeluar, context: Context) {
            binding.apply {
                itemView.setOnClickListener { listener?.invoke(data.idSurat) }
                tvNoSurat.text = if(data.noSurat.isNullOrEmpty()) "-" else data.noSurat
                tvPerihal.text = data.perihal
                tvInstansiPengirim.text = data.instansiPenerima
                tvTglSurat.text = Constants.showDate(data.tglSurat, true)
                tvStatusSurat.text = data.statusSurat
                layoutStatusSurat.backgroundTintList =
                    when(data.statusSurat){
                        "Diterima" -> ColorStateList.valueOf(
                            context.resources.getColor(R.color.green_success))
                        "Dikoreksi" -> ColorStateList.valueOf(
                            context.resources.getColor(com.uk.tastytoasty.R.color.yellow))
                        else -> ColorStateList.valueOf(
                            context.resources.getColor(R.color.red_warning))
                    }
                
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvSuratMasukViewHolder {
        context = parent.context
        val view = CardSuratMasukBinding.inflate(LayoutInflater.from(context), parent, false)
        return RvSuratMasukViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: RvSuratMasukViewHolder, position: Int) {
        holder.bind(list[position], context)
    }
    
    override fun getItemCount(): Int {
        return list.size
    }
    
    
}