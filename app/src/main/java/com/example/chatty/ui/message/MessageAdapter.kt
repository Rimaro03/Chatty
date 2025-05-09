package com.example.chatty.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.example.chatty.models.Message

class MessageAdapter(private var messageList: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class OutgoingMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageTV: TextView = itemView.findViewById(R.id.out_msg_tv)

        fun bind(message: Message){
            messageTV.text = message.content
        }
    }

    class IncomingMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageTV: TextView = itemView.findViewById(R.id.in_msg_tv)

        fun bind(message: Message){
            messageTV.text = message.content
        }
    }

    fun submitList(newList: List<Message>) {
        messageList = newList
        notifyItemRangeInserted(0, newList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            VIEW_TYPE_INCOMING -> {
                val view = layoutInflater.inflate(R.layout.in_msg_item, parent, false)
                IncomingMessageViewHolder(view)
            }
            VIEW_TYPE_OUTGOING -> {
                val view = layoutInflater.inflate(R.layout.out_msg_item, parent, false)
                OutgoingMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is OutgoingMessageViewHolder -> holder.bind(messageList[position])
            is IncomingMessageViewHolder -> holder.bind(messageList[position])
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isIncoming) VIEW_TYPE_INCOMING else VIEW_TYPE_OUTGOING
    }

    companion object {
        private const val VIEW_TYPE_INCOMING = 0
        private const val VIEW_TYPE_OUTGOING = 1
    }
}