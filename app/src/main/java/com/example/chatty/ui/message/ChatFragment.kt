package com.example.chatty.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChatFragment: Fragment() {
    private val messageViewModel: MessageViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // get contact id from HomeFragment
        val contactId = arguments?.getString("contactId")?.toLong()
        if(contactId == null) {
            requireActivity().finishAndRemoveTask()
            return null
        }
        // if it's not null, set it in the viewmodel
        messageViewModel.setChatId(contactId)

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // Toolbar
        val toolBar = view.findViewById<Toolbar>(R.id.chat_toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolBar)

        // Bind navcontroller to toolbar for back button
        val navController = NavHostFragment.findNavController(this)
        if(navController.graph.startDestinationId != 0) {
            toolBar.setNavigationIcon(R.drawable.arrow_back)
            toolBar.setNavigationOnClickListener {
                navController.navigateUp()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // message RecyclerView
        val messageRecyclerView = view.findViewById<RecyclerView>(R.id.message_rv_list)
        messageRecyclerView.layoutManager = LinearLayoutManager(view.context).apply { stackFromEnd = true }
        val adapter = MessageAdapter(mutableListOf())
        messageRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                messageViewModel.chatMessages.observe(viewLifecycleOwner) { newList ->
                    adapter.submitList(newList)
                    messageRecyclerView.scrollToPosition(newList.size - 1)
                }
            }
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

        // I send a notification with the contact icon when user clicks on it
        contactIcon.setOnClickListener {
            messageViewModel.showIconNotification(view.context)
        }

        val callButton = view.findViewById< ImageButton>(R.id.toolbar_call_button)
        callButton.setOnClickListener {
            messageViewModel.startFakeCall(view.context)
        }

        val mediaButton = view.findViewById<ImageButton>(R.id.toolbar_media_button)
        mediaButton.setOnClickListener {
            messageViewModel.playAudio()
        }
    }

    override fun onPause() {
        super.onPause()
        // Letting viewmodel know fragment is hidden
        messageViewModel.onFragmentHidden()
    }
}