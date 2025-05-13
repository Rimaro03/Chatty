package com.example.chatty.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.chatty.models.Chat
import com.example.chatty.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    chatRepository: ChatRepository
): ViewModel() {
    var chatList: LiveData<List<Chat>> = chatRepository.getChats()
}