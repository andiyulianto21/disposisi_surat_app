package com.daylantern.arsipsuratpembinaan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.OnItemClickListener
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.databinding.CardSuratMasukBinding
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk

class RvSuratMasukAdapter(val list: List<SuratMasuk>): RecyclerView.Adapter<RvSuratMasukAdapter.RvSuratMasukViewHolder>() {

    private lateinit var context: Context
    private var listener: ((Int) -> Unit)? = null

    fun setOnClickListener(action: (Int) -> Unit){
        this.listener = action
    }

    class RvSuratMasukViewHolder(val binding: CardSuratMasukBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvSuratMasukViewHolder {
        context = parent.context
        val view = CardSuratMasukBinding.inflate(LayoutInflater.from(context), parent, false)
        return RvSuratMasukViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvSuratMasukViewHolder, position: Int) {
        holder.itemView.setOnClickListener{
            listener?.invoke(position)
        }
        val calendar = Constants.convertDateStringToCalendar(list[position].tglSuratDiterima, false)
        holder.binding.apply {
            tvSifatSurat.text = list[position].sifatSurat
            tvNoSurat.text = list[position].noSuratMasuk
            tvPerihal.text = list[position].perihal
            tvInstansiPengirim.text = list[position].instansiPengirim
            tvTglSurat.text = Constants.showDate(calendar, false)
            if(list[position].statusSurat == "MENUNGGU_DISPOSISI"){
                imgStatusSurat.setBackgroundColor(context.resources.getColor(R.color.purple_700))
            }else {
                imgStatusSurat.setBackgroundColor(context.resources.getColor(R.color.purple_200))
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}