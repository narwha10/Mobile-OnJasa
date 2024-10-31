package com.example.onjasa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private var orders: List<Pair<String, String>>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvOrderTitle)
        val time: TextView = itemView.findViewById(R.id.tvOrderTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val (title, time) = orders[position]
        holder.title.text = title
        holder.time.text = time
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Pair<String, String>>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
