package com.passerby.metrograd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ContactsActivity : AppCompatActivity() {
    var userStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        supportActionBar?.hide()

        val intent = intent
        userStatus = intent.getStringExtra("userStatus").toString()

    }

    fun backToMonitors(view: View){
        val intent = Intent(this, ReadyMonitorActivity::class.java)
        intent.putExtra("userStatus", userStatus)
        startActivity(intent)
    }
}