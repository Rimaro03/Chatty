package com.example.chatty.ui.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.example.chatty.models.Message
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment: Fragment() {
    private val messageViewModel: MessageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get contact id from HomeFragment
        messageViewModel.setContactId(arguments?.getLong("contactId") ?: 0L)

        // message RecyclerView
        val messageRecyclerView = view.findViewById<RecyclerView>(R.id.message_rv_list)
        messageRecyclerView.layoutManager = LinearLayoutManager(view.context).apply { stackFromEnd = true }
        val adapter = MessageAdapter(mutableListOf())
        messageRecyclerView.adapter = adapter

        messageViewModel.chatMessages.observe(viewLifecycleOwner) { newList ->
            messageRecyclerView.scrollToPosition(newList.size - 1)
            adapter.submitList(newList)
        }

        //input field
        val textInput = view.findViewById<TextInputLayout>(R.id.message_input)
        val sendButton = view.findViewById<Button>(R.id.send_btn)

        sendButton.setOnClickListener {
            if(textInput.editText?.text.toString().isEmpty()) return@setOnClickListener
            val message = textInput.editText?.text.toString()
            messageViewModel.sendMessage(content = message)
            textInput.editText?.text?.clear()
        }
    }
}