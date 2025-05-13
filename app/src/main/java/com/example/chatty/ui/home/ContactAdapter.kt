package com.example.chatty.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.example.chatty.models.Chat
import com.google.android.material.imageview.ShapeableImageView

class ContactAdapter(private var chatList: List<Chat>): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val contactTv: TextView = itemView.findViewById(R.id.contact_name_tv)
        private val contactDescriptionTv: TextView = itemView.findViewById(R.id.contact_description_tv)
        private val contactIcon: ShapeableImageView = itemView.findViewById(R.id.contact_img)

        fun bind(chat: Chat) {
            contactTv.text = chat.name
            // set image with contact.icon
            Log.d("ContactAdapter", "contact.icon: ${R.drawable.boneca}")
            itemView.setOnClickListener {
                val navController = itemView.findNavController()
                val deepLinkReq = NavDeepLinkRequest.Builder
                    .fromUri("chatty://chat/${chat.id}".toUri())
                    .build()
                navController.navigate(deepLinkReq)
            }
            contactDescriptionTv.text = itemView.context.getString(R.string.contact_description, chat.name)
            contactIcon.setImageResource(chat.icon)
        }
    }

    fun submitList(newList: List<Chat>) {
        chatList = newList
        notifyItemRangeInserted(0, newList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size
}