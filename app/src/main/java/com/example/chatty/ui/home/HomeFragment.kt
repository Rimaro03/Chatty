package com.example.chatty.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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