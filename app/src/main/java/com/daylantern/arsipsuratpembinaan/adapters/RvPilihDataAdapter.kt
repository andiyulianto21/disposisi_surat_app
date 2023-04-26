package com.daylantern.arsipsuratpembinaan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daylantern.arsipsuratpembinaan.OnItemClickListener
import com.daylantern.arsipsuratpembinaan.databinding.CardPilihDataBinding
import com.daylantern.arsipsuratpembinaan.models.PilihData

class RvPilihDataAdapter(var items: List<PilihData>) :
    RecyclerView.Adapter<RvPilihDataAdapter.RvPilihDataHolder>() {

    private lateinit var context: Context
    private var listener: OnItemClickListener? = null

    fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun getSelectedItem(): List<PilihData> {
        val selected = mutableListOf<PilihData>()
        items.filter { it.isChecked }.forEach {
            selected.add(it)
        }
        return selected
    }

    fun resetSelected() {
        items.forEach { it.isChecked = false }
        notifyDataSetChanged()
    }

    inner class RvPilihDataHolder(val binding: CardPilihDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PilihData) {
            if (item.isChecked) {
                itemView.visibility = View.GONE
            } else {
                itemView.visibility = View.VISIBLE
            }
            binding.tvNamaData.text = item.title

            binding.cbTerpilih.isChecked = item.isChecked
            if (item.isChecked) {
                itemView.visibility = View.GONE
            } else {
                itemView.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                if (item.isChecked) {
                    binding.cbTerpilih.isChecked = false
                    item.isChecked = false
                } else {
                    binding.cbTerpilih.isChecked = true
                    item.isChecked = true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvPilihDataHolder {
        context = parent.context
        val view = CardPilihDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RvPilihDataHolder(view)
    }

    override fun onBindViewHolder(holder: RvPilihDataHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}