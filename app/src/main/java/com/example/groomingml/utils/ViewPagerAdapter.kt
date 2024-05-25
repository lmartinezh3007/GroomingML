package com.example.groomingml.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groomingml.R

class ViewPagerAdapter: RecyclerView.Adapter<ViewHolderCustom>() {

    private val itemList = listOf("Make sure social media accounts are set to private to limit access to personal information",
                                    "Never share personal details like address, phone number, or school name online",
                                        "Only accept friend requests from people you know and trust in real life")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCustom {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_layout, parent, false)
        return ViewHolderCustom(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolderCustom, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

}