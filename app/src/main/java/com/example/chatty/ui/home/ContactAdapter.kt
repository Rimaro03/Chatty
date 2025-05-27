package com.example.chatty.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.example.chatty.models.Chat
import com.example.chatty.models.ChatWithLastMessage
import com.google.android.material.imageview.ShapeableImageView

class ContactAdapter(
    private var chatList: List<ChatWithLastMessage>,
    val onChatClicked: (Chat) -> Unit
): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    class ContactViewHolder(
        itemView: View,
    ): RecyclerView.ViewHolder(itemView){
        private val contactTv: TextView = itemView.findViewById(R.id.contact_name_tv)
        private val contactLastMsgTv: TextView = itemView.findViewById(R.id.contact_last_msg_tv)
        private val contactIcon: ShapeableImageView = itemView.findViewById(R.id.contact_img)

        fun bind(chatWithLastMessage: ChatWithLastMessage, onChatClicked: (Chat) -> Unit) {
            val chat = chatWithLastMessage.chat
            val lastMessage = chatWithLastMessage.messageContent
            contactTv.text = chat.name
            // set image with contact.icon
            itemView.setOnClickListener {
                onChatClicked(chat)
                Log.d("ContactAdapter", "onChatClicked: $chat")
            }
            // trim last message to 30 characters
            contactLastMsgTv.text = lastMessage?.take(30)
            contactIcon.setImageResource(chat.icon)
        }
    }

    // TODO: use better function that notifyDataSetChanged()
    fun submitList(newList: List<ChatWithLastMessage>) {
        chatList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(chatList[position], onChatClicked)
    }

    override fun getItemCount(): Int = chatList.size
}