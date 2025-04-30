package com.example.chatty.data

fun messageList(): List<Message> {
    return listOf(
        Message(
            "Hi there!",
            true
        ),
        Message(
            "Hi, what's up?",
            false
        ),
        Message(
            "U free this friday night?",
            true
        ),
        Message(
            "Guess so",
            false
        )
    )
}