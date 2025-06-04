package com.example.kiosk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony.Mms.Intents
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ManagerRegisterPage : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imgView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.manager_register_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.managerRegisterPage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        
        //저장부분
        val confirmBtn = findViewById<Button>(R.id.managerRegisterPage_confirmBtn);
        val cancelBtn = findViewById<Button>(R.id.managerRegisterPage_cancelBtn);

        confirmBtn.setOnClickListener(){

        }

        cancelBtn.setOnClickListener(){
            finish()
        }



        //이미지 입력
        val imgBtn = findViewById<Button>(R.id.managerRegisterPage_imgBtn);
        imgView = findViewById(R.id.managerRegisterPage_img);

        imgBtn.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                val selectedImageUri: Uri? = data.data
                imgView.setImageURI(selectedImageUri)
            }
        }
    }
}