package com.bitlove.fetlife.common.logic.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil

class BindableRecyclerAdapter<T : BindableRecyclerAdapter.Diffable, H>(
        private val itemLayout: Int,
        private val itemHandler: H,
        private val itemBindingId: Int,
        private val handlerBindingId: Int
) : RecyclerView.Adapter<BindableRecyclerAdapter.BindableViewHolder<T,H>>() {

    private var items: MutableList<T> = ArrayList()

    fun setItems(newItems: List<T>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallBack(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BindableViewHolder<T,H>, position: Int) {
        holder.bind(items[position],itemHandler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder<T,H> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), itemLayout, parent, false)
        return BindableViewHolder(binding, itemBindingId, handlerBindingId)
    }

    interface Diffable {
        fun isSame(other: Diffable): Boolean
        fun hasSameContent(other: Diffable): Boolean
    }

    class BindableViewHolder<T,H>(private val binding: ViewDataBinding, private val itemBindingId: Int, private val handlerBindingId: Int) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, itemHandler: H) {
            binding.setVariable(itemBindingId, item)
            binding.setVariable(handlerBindingId, itemHandler)
        }
    }

    class DiffUtilCallBack<T : Diffable>(private val oldItems: List<T>, private val newItems: List<T>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size
        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition].isSame(newItems[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition].hasSameContent(newItems[newItemPosition])
    }

}