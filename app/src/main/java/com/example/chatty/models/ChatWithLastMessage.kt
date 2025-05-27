package com.example.chatty.models
import androidx.room.ColumnInfo
import androidx.room.Embedded


data class ChatWithLastMessage(
    @Embedded
    val chat: Chat,

    @ColumnInfo(name = "messageContent")
    val messageContent: String?,

    @ColumnInfo(name = "messageRead")
    val messageRead: Boolean?
)