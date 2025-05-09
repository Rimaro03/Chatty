package com.example.chatty.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.example.chatty.models.Contact

class ContactAdapter(private var contactList: List<Contact>): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val contactTv: TextView = itemView.findViewById(R.id.contact_tv)

        fun bind(contact: Contact) {
            contactTv.text = contact.name
            contactTv.setOnClickListener {
                val navController = itemView.findNavController()
                // TODO: navigate to chat fragment
                // TODO: pass contactId to chat fragment
            }
        }
    }

    fun submitList(newList: List<Contact>) {
        contactList = newList
        notifyItemRangeInserted(0, newList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    override fun getItemCount(): Int = contactList.size
}