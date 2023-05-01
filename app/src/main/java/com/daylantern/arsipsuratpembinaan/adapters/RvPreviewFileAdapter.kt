package com.daylantern.arsipsuratpembinaan.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.daylantern.arsipsuratpembinaan.databinding.CardPreviewFileBinding
import com.daylantern.arsipsuratpembinaan.models.FileSuratModel

class RvPreviewFileAdapter(private val items: MutableList<FileSuratModel>):
    RecyclerView.Adapter<RvPreviewFileAdapter.RvPreviewFileViewHolder>() {

    interface Listener {
        fun onItemClicked(image: Bitmap)
        fun onItemRemoved()
    }

    private var listener: Listener? = null
    private lateinit var context: Context

    fun setOnItemClicked(listener: Listener){
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(item: FileSuratModel) {
        items.add(item)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyDataSetChanged()
    }

    fun getAll(): List<FileSuratModel> {
        return items.toList()
    }

    inner class RvPreviewFileViewHolder(val binding: CardPreviewFileBinding): ViewHolder(binding.root){
        fun bind(position: Int){
            val item = items[position]

            binding.apply {
                imgFileSurat.setImageBitmap(item.bitmap)
                itemView.setOnClickListener { listener?.onItemClicked(item.bitmap) }
                imgHapusFile.setOnClickListener {
                    removeItem(position)
                    listener?.onItemRemoved()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvPreviewFileViewHolder {
        context = parent.context
        val view = CardPreviewFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RvPreviewFileViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvPreviewFileViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}