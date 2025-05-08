package com.example.chatty.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Message (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val content: String,
    val mediaUri: String? = null,
    val mediaMimeType: String? = null,
    val timestamp: Long? = null,
    val contactId: Long
) {

    // contact with 0L is me
    val isIncoming: Boolean
        get() = contactId != 0L
}