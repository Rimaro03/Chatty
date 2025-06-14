package com.example.chatty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class BubbleActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bubble)

        val contactId = intent.getLongExtra("contactId", 0)
        title = "Chatty"

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        val navController = navHostFragment.navController

        if (savedInstanceState == null) {
            // Optionally pass intent extras to start destination
            navController.setGraph(R.navigation.nav_graph, intent.extras ?: Bundle())
        }

        val uri = "chatty://chat/$contactId".toUri()
        navController.navigate(uri)
    }
}