package com.example.chatty

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.data.Message

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class OutgoingMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageTV: TextView = itemView.findViewById(R.id.out_msg_tv)

        fun bind(index: Int, message: Message){
            messageTV.text = message.content
        }
    }

    class IncomingMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageTV: TextView = itemView.findViewById(R.id.in_msg_tv)

        fun bind(index: Int, message: Message){
            messageTV.text = message.content
        }
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
            is OutgoingMessageViewHolder -> holder.bind(position, messageList[position])
            is IncomingMessageViewHolder -> holder.bind(position, messageList[position])
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