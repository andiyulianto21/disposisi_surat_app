package com.daylantern.arsipsuratpembinaan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.databinding.CardPilihDataBinding
import com.daylantern.arsipsuratpembinaan.databinding.CardPilihSatuDataBinding
import com.daylantern.arsipsuratpembinaan.models.PilihData

class RvPilihDataAdapter(var items: List<PilihData>) :RecyclerView.Adapter<RvPilihDataAdapter.RvPilihDataHolder>() {

    private lateinit var context: Context
    private var isChecked: Boolean = false

    class RvPilihDataHolder(val binding: CardPilihSatuDataBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvPilihDataHolder {
        context = parent.context
        val view = CardPilihSatuDataBinding.inflate(LayoutInflater.from(parent.context))
        return RvPilihDataHolder(view)
    }

    override fun onBindViewHolder(holder: RvPilihDataHolder, position: Int) {

        val item = items[position]
        holder.binding.apply {
            tvNamaData.text = item.title
            root.setOnClickListener {
                if(isChecked){
                    cbTerpilih.isChecked = false
                    cbTerpilih.visibility = View.INVISIBLE
                    it.setBackgroundColor(context.resources.getColor(R.color.white))
                }else {
                    cbTerpilih.isChecked = true
                    cbTerpilih.visibility = View.VISIBLE
                    it.setBackgroundColor(context.resources.getColor(R.color.purple_200))
                }
                isChecked = !isChecked
            }
        }



//        val item = items[position]
//        holder.binding.apply {
//            tvNamaData.text = item.title
//            cbTerpilih.isChecked = item.isChecked
//            cbTerpilih.setOnCheckedChangeListener { _, bool ->
//                if(bool){
//                    cbTerpilih.visibility = View.VISIBLE
//                }else {
//                    cbTerpilih.visibility = View.INVISIBLE
//                }
//                item.isChecked = bool
//                holder.binding.cbTerpilih.isChecked = bool
//            }
//        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}