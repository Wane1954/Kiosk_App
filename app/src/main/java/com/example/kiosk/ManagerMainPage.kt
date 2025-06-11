package com.example.kiosk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ManagerMainPage : AppCompatActivity(){
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

        //처음 생성
        val container = findViewById<GridLayout>(R.id.productContainer)
        val inflater = layoutInflater

        val productList = ProductStorage(this).getAllProducts()
        for (product in productList) {
            val itemView = inflater.inflate(R.layout.manager_item_product, container, false)

            val imgView = itemView.findViewById<ImageView>(R.id.manager_item_img)
            val nameView = itemView.findViewById<TextView>(R.id.manager_item_name)
            val priceView = itemView.findViewById<TextView>(R.id.manager_item_price)
            val stockView = itemView.findViewById<TextView>(R.id.manager_item_stock)

            nameView.setText(product.name)
            priceView.setText("가격: ${product.price}원")
            stockView.setText("재고: ${product.inStock} / ${product.stock}")

            if (product.imgPath.isNotEmpty()) {
                imgView.setImageURI(Uri.parse(product.imgPath))
            }

            container.addView(itemView)
        }
        
        //버튼
        val backBtn = findViewById<Button>(R.id.managerMainPage_backBtn);
        val registeBtn = findViewById<Button>(R.id.managerMainPage_registerBtn);

        backBtn.setOnClickListener(){
            finish()
        }

        registeBtn.setOnClickListener(){
            val intent = Intent(this,ManagerRegisterPage::class.java)
            startActivity(intent)
            finish()
        }


    }
}