package com.example.chatty.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.google.android.material.imageview.ShapeableImageView
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

        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.chat_toolbar))
        // Bind navcontroller to toolbar for back button
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        view.findViewById<Toolbar>(R.id.chat_toolbar)
            .setupWithNavController(navController = navController, configuration = appBarConfiguration)

        // Letting the viewmodel know this fragment visible
        messageViewModel.onFragmentVisible()

        // get contact id from HomeFragment
        messageViewModel.setChatId(arguments?.getString("contactId")?.toLong() ?: 0L)

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

        // Load icon and name in appbar
        val contactIcon: ShapeableImageView = view.findViewById(R.id.toolbar_contact_img)
        val contactName: TextView = view.findViewById(R.id.toolbar_contact_name_tv)
        messageViewModel.chat.observe(viewLifecycleOwner) { chat ->
            contactIcon.setImageResource(chat.icon)
            contactName.text = chat.name
        }
    }

    override fun onPause() {
        super.onPause()
        // Letting viewmodel know fragment is hidden
        messageViewModel.onFragmentHidden()
    }
}