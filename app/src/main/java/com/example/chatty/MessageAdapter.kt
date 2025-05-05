package com.example.chatty

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.data.Message

class MessageAdapter(private val messageList: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageTV: TextView = itemView.findViewById(R.id.message_tv)

        fun bind(index: Int, message: Message){
            messageTV.text = message.content

            // change constraints to place incoming message on the left
            if(message.isIncoming) {
                val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.message_layout)
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)

                constraintSet.clear(R.id.message_tv, ConstraintSet.START)
                constraintSet.clear(R.id.message_tv, ConstraintSet.END)

                constraintSet.connect(R.id.message_tv, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.connect(R.id.message_tv, ConstraintSet.END, R.id.incoming_msg_guideline, ConstraintSet.START)

                constraintSet.applyTo(constraintLayout)

                messageTV.gravity = Gravity.START
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