package com.example.chatty

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.data.Message

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageTV: TextView = itemView.findViewById(R.id.message_tv)

        fun bind(index: Int, message: Message){
            messageTV.text = message.content
            if(message.isIncoming) {
                messageTV.gravity = Gravity.START

            } else {
                messageTV.gravity = Gravity.END
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(position, messageList[position])
    }

    override fun getItemCount(): Int = messageList.size
}