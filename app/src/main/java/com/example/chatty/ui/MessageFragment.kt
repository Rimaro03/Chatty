package com.example.chatty.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.MessageAdapter
import com.example.chatty.ui.MessageViewModel
import com.example.chatty.R
import com.example.chatty.model.Message
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageFragment: Fragment() {
    private val messageViewModel: MessageViewModel by viewModels()

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
        val textInput = view.findViewById<TextInputLayout>(R.id.message_input)

        val sendButton = view.findViewById<Button>(R.id.send_btn)

        sendButton.setOnClickListener {
            if(textInput.editText?.text.toString().isEmpty()) return@setOnClickListener

            val message = textInput.editText?.text.toString()
            messageViewModel.sendMessage(Message(
                id = 1L,
                contactId = 2L,
                content = message
            ))
            textInput.editText?.text?.clear()
        }

        return view
    }
}