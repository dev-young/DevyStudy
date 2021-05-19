package io.ymsoft.devystudy.tourapi.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.ymsoft.devystudy.databinding.ItemTourBinding
import io.ymsoft.devystudy.tourapi.models.Tour

class TourListAdapter(
    var clickListener: ((Int, Tour) -> Unit)? = null,
    var longClickListener: ((Int, Tour) -> Boolean)? = null
) : ListAdapter<Tour, TourListAdapter.TourViewHolder>(object : DiffUtil.ItemCallback<Tour>() {
    override fun areItemsTheSame(oldItem: Tour, newItem: Tour): Boolean {
        return oldItem.contenid == newItem.contenid
    }

    override fun areContentsTheSame(oldItem: Tour, newItem: Tour): Boolean {
        return oldItem == newItem
    }

}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTourBinding.inflate(layoutInflater, parent, false)
        return TourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        val tour = getItem(position)
        holder.onBind(tour)
    }

    inner class TourViewHolder (
        val binding: ItemTourBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(model: Tour) {
            binding.title.text = model.title
            binding.address.text = model.getAddress()
            binding.root.setOnClickListener { clickListener?.invoke(adapterPosition, model) }
            binding.root.setOnLongClickListener {
                longClickListener?.invoke(adapterPosition, model) ?: false
            }
        }

    }

}