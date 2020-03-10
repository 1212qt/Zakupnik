package com.mat.zakupnik.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mat.zakupnik.R
import com.mat.zakupnik.interfaces.IDeletionNotifier

class ToBuyAdapter(private val list : MutableList<String>, private val notifierI: IDeletionNotifier)
    : RecyclerView.Adapter<ToBuyAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val text : TextView = itemView.findViewById(R.id.item_to_buy_tv_text)
        val deleteButton : Button = itemView.findViewById(R.id.item_to_buy_btn_delete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_to_buy, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = holder.text
        val deleteButton = holder.deleteButton

        text.text = list[position]
        deleteButton.setOnClickListener {notifierI.notifyItemDeleted(position)}
    }

    override fun getItemCount(): Int = list.size


}