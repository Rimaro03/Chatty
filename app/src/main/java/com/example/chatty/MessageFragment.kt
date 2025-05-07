package com.example.chatty

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.data.Message
import kotlin.getValue

class MessageFragment: Fragment() {
    private val messageViewModel: MessageViewModel by viewModels{ MessageViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // message RecyclerView
        val messageRecyclerView = view.findViewById<RecyclerView>(R.id.message_rv_list)
        messageRecyclerView.layoutManager = LinearLayoutManager(view.context).apply { stackFromEnd = true }
        val adapter = MessageAdapter(mutableListOf<Message>())
        messageRecyclerView.adapter = adapter

        messageViewModel.messageList.observe(viewLifecycleOwner) { newList ->
            adapter.submitList(newList)
        }

        //input field
        val messageInput = view.findViewById<EditText>(R.id.message_input)
        val sendButton = view.findViewById<Button>(R.id.send_btn)

        sendButton.setOnClickListener {
            if(messageInput.text.toString().isEmpty()) return@setOnClickListener

            val message = messageInput.text.toString()
            messageViewModel.sendMessage(Message(message, false))
            messageInput.text.clear()
        }

        return view
    }
}