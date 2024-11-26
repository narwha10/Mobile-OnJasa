package com.example.onjasa.models

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.onjasa.R
import com.example.onjasa.databinding.ItemMessageBinding

class ChatAdapter(private val userId: String) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<Message>()

    // ViewHolder untuk setiap item pesan
    inner class MessageViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.messageContent.text = message.content

            // Tentukan apakah pesan ini dikirim oleh user atau diterima
            if (message.senderId == userId) {
                // Pesan dikirim oleh user, tampilkan di kanan
                binding.messageContainer.gravity = Gravity.END
                binding.messageContent.setBackgroundResource(R.drawable.outline_constraint) // Bubble kanan untuk pesan yang dikirim
                binding.profileImage.visibility = View.GONE  // Tidak menampilkan profil gambar pengirim di kanan
            } else {
                // Pesan diterima oleh user, tampilkan di kiri
                binding.messageContainer.gravity = Gravity.START
                binding.messageContent.setBackgroundResource(R.drawable.bubble_right) // Bubble kiri untuk pesan yang diterima
                binding.profileImage.visibility = View.VISIBLE  // Menampilkan profil gambar penerima
            }
        }
    }

    // Inflate item layout untuk setiap ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    // Bind data untuk setiap item pesan
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    // Return jumlah item pesan
    override fun getItemCount(): Int = messages.size

    // Fungsi untuk memperbarui pesan
    fun updateMessages(newMessages: List<Message>) {
        val diffCallback = MessageDiffCallback(messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        messages.clear()
        messages.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    // Fungsi untuk menambahkan pesan baru
    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}

class MessageDiffCallback(
    private val oldList: List<Message>,
    private val newList: List<Message>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    // Membandingkan apakah item yang sama berdasarkan id
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    // Membandingkan apakah konten pesan sama
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
