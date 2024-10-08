package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GetStartedActivity : AppCompatActivity() {

    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        // Inisialisasi button
        btnStart = findViewById(R.id.btnstart)

        // Set onClickListener untuk button
        btnStart.setOnClickListener {
            // Arahkan ke HomeActivity
            val intent = Intent(this@GetStartedActivity, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}
