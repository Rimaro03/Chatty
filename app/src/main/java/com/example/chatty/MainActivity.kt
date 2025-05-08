package com.example.chatty
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Check for notifications permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED)
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        val p = grantResults[0] == PermissionChecker.PERMISSION_GRANTED
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PermissionChecker.PERMISSION_GRANTED) {
            // not necessary to kill the app... but for now it's fine
            this.finishAffinity()
        }
        Log.i(TAG, "Notification runtime permission granted: $p")
    }

    companion object
    {
        // Request code for the POST_NOTIFICATIONS permission
        private const val REQUEST_CODE = 21983

        // Logcat tag
        private val TAG = MainActivity::class.simpleName
    }
}