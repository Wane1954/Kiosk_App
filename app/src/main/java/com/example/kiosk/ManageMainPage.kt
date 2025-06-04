package com.example.kiosk

import android.content.Intent
import android.os.Bundle
import android.provider.Telephony.Mms.Intents
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ManageMainPage : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.manager_main_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.managerMainPage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<Button>(R.id.managerMainPage_backBtn);
        val registeBtn = findViewById<Button>(R.id.managerMainPage_registerBtn);

        backBtn.setOnClickListener(){
            finish()
        }

        registeBtn.setOnClickListener(){
            val intent = Intent(this,ManagerRegisterPage::class.java)
            startActivity(intent)
        }
    }
}