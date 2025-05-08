package com.example.chatty.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact (
    @PrimaryKey
    val id: Long,
    val name: String,
    val icon: String,
)