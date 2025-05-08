package com.example.chatty.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val contactId: Long,
)