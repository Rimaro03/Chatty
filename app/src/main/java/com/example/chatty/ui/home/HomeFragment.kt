package com.example.chatty.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.example.chatty.models.Chat
import com.example.chatty.models.ChatWithLastMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Toolbar
        val toolBar = view.findViewById<Toolbar>(R.id.home_toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolBar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add menu to toolbar
        val menuButton = view.findViewById<ImageButton>(R.id.toolbar_more_button)
        menuButton.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            popup.menuInflater.inflate(R.menu.home_toolbar_popup, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete_history -> {
                        homeViewModel.clearHistory()
                        Toast.makeText(requireContext(), "Message history cleared", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        // contact RecyclerView
        val navController = findNavController()
        val contactRecyclerView = view.findViewById<RecyclerView>(R.id.contact_rv_list)
        contactRecyclerView.layoutManager = LinearLayoutManager(view.context)
        fun onContactClick(chat: Chat) {
            Log.d("HomeFragment", "navigated");
            val deepLinkReq = NavDeepLinkRequest.Builder
                .fromUri("chatty://chat/${chat.id}".toUri())
                .build()
            navController.navigate(deepLinkReq)
        }
        // passing a reference to the onContactClick function to the ContactAdapter
        val adapter = ContactAdapter(mutableListOf<ChatWithLastMessage>(), ::onContactClick)
        contactRecyclerView.adapter = adapter
        homeViewModel.chatWithLastMessageList.observe(viewLifecycleOwner) { chatList ->
            Log.d("HomeFragment", "chatList: $chatList")
            // Disabling chat with myself (0L is my ID)
            adapter.submitList(chatList.filter { it.chat.id != 0L })
        }
    }
}