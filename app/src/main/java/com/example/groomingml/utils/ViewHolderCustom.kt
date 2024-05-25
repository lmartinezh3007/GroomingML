package com.example.groomingml.utils

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groomingml.R

class ViewHolderCustom(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val pagerText: TextView = itemView.findViewById(R.id.pagerText)

    fun bind(item: String) {
        pagerText.text = item
    }
}