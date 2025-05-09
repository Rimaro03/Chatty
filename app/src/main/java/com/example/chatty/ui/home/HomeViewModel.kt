package com.example.chatty.ui.home

import androidx.lifecycle.ViewModel
import com.example.chatty.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    chatRepository: ChatRepository
): ViewModel() {
    val contactList = chatRepository.getContacts()
}