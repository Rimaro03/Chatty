package com.example.chatty.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.chatty.models.Contact
import com.example.chatty.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    chatRepository: ChatRepository
): ViewModel() {
    var contactList: LiveData<List<Contact>> = chatRepository.getContacts
}