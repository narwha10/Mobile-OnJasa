package com.example.onjasa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Order(
    val title: String,
    val time: String,
    val iconResId: Int // ID resource untuk ikon
)

class OrderAdapter(private var orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderTitle.text = order.title
        holder.tvOrderTime.text = order.time
        holder.ivIcon.setImageResource(order.iconResId)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderTitle: TextView = itemView.findViewById(R.id.tvOrderTitle)
        val tvOrderTime: TextView = itemView.findViewById(R.id.tvOrderTime)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
    }
}
