package com.agooday.baseapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FastAdapter<T>(
    private val items: List<T>,
    private val layoutIds: ArrayList<Int>,
    private val onBindView: OnBindView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val DATA_TYPE = 0
        val HEADER_TYPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layoutIds[viewType],
                parent,
                false
            )
        )
    }
    override fun getItemViewType(position: Int): Int {
        return onBindView.getViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindView.onBindView(getItemViewType(position), position, holder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnBindView {
        fun onBindView(type: Int, i: Int, holder: RecyclerView.ViewHolder)
        fun getViewType(position: Int):Int
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}