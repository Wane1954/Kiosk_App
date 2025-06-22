package com.example.kiosk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.io.File
import java.text.NumberFormat
import java.util.Locale

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false) // 시스템 바 영역을 뷰가 차지하게 함
            window.insetsController?.apply {
                // 상태바와 네비게이션 바 숨기기
                hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())

                // 바를 스와이프로 잠깐 보여주도록 설정
                systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // API 30 미만은 legacy 방식 사용
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                            or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
        
        //버튼
        val backBtn = findViewById<Button>(R.id.managerMainPage_backBtn)
        val registeBtn = findViewById<Button>(R.id.managerMainPage_registerBtn)
        val historyBtn = findViewById<Button>(R.id.managerMainPage_historyBtn)
        val paymentBtn = findViewById<Button>(R.id.managerMainPage_paymentBtn)

        backBtn.setOnClickListener(){
            finish()
        }

        registeBtn.setOnClickListener(){
            val intent = Intent(this,ManagerRegisterPage::class.java)
            startActivity(intent)
        }

        historyBtn.setOnClickListener(){
            val intent = Intent(this,ManagerPurchaseHistoryPage::class.java)
            startActivity(intent)
        }

        paymentBtn.setOnClickListener(){
            val intent = Intent(this,ManagerPaymentMethodPage::class.java)
            startActivity(intent)
        }
    }
    
    //화면으로 돌아올 때마다 호출되는 함수
    override fun onResume() {
        super.onResume()
        refreshProductList()
    }

    // 목록 새로고침 함수
    fun refreshProductList(){

        val container = findViewById<GridLayout>(R.id.managerMainPage_productContainer)
        container.removeAllViews() //생성된 목록 삭제
        
        val inflater = layoutInflater

        val productList = ProductStorage(this).getAllProducts()
        for (product in productList) {
            val itemView = inflater.inflate(R.layout.manager_item_product, container, false)

            val imgView = itemView.findViewById<ImageView>(R.id.manager_item_img)
            val nameView = itemView.findViewById<TextView>(R.id.manager_item_name)
            val priceView = itemView.findViewById<TextView>(R.id.manager_item_price)
            val stockView = itemView.findViewById<TextView>(R.id.manager_item_stock)

            nameView.setText(product.name)

            val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
            val formattedPrice = numberFormat.format(product.price) + "원"
            priceView.setText(formattedPrice)
            stockView.setText("재고: ${product.inStock} / ${product.stock}")

            if (product.imgPath.isNotEmpty()) {
                val file = File(product.imgPath)
                if (file.exists()) {
                    Glide.with(this)
                        .load(file)
                        .placeholder(R.drawable.no_img)
                        .error(R.drawable.no_img)
                        .signature(com.bumptech.glide.signature.ObjectKey(file.lastModified())) //캐시 무효화
                        .into(imgView)
                } else {
                    imgView.setImageResource(R.drawable.no_img)
                }
            }


            Log.d("ImagePath", "Loading image path: ${product.imgPath}")

            //클릭 리스너 추가
            itemView.setOnClickListener {
                val intent = Intent(this, ManagerEditPage::class.java)
                intent.putExtra("product", product)  // Serializable 전달
                startActivity(intent)
            }

            container.addView(itemView)
        }
    }
}