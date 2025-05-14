package com.example.chatty.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.example.chatty.models.Chat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.home_toolbar))
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
        val contactRecyclerView = view.findViewById<RecyclerView>(R.id.contact_rv_list)
        contactRecyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapter = ContactAdapter(mutableListOf<Chat>())
        contactRecyclerView.adapter = adapter
        homeViewModel.chatList.observe(viewLifecycleOwner) { chatList ->
            Log.d("HomeFragment", "chatList: $chatList")
            // Disabling chat with myself (0L is my ID)
            adapter.submitList(chatList.dropWhile { contact -> contact.id == 0L })
        }
    }
}