package com.example.kiosk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mainManageBtn = findViewById<Button>(R.id.mainManageBtn);
        val mainConsumerBtn = findViewById<Button>(R.id.mainConsumerBtn);

        mainManageBtn.setOnClickListener(){
            val intent = Intent(this,ManagerMainPage::class.java)
            startActivity(intent)
        }

        mainConsumerBtn.setOnClickListener(){
            val intent = Intent(this,ConsumerMainPage::class.java)
            startActivity(intent)
        }

    }
}