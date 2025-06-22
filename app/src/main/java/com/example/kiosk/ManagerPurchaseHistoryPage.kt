package com.example.kiosk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManagerPurchaseHistoryPage: AppCompatActivity(){

    private lateinit var storage: ProductPurchaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.manager_purchase_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.managerPurchaseHistory)) { v, insets ->
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



        //본동작
        val backBtn = findViewById<Button>(R.id.managerPurchaseHistoryPage_backBtn)
        val clearBtn = findViewById<Button>(R.id.managerPurchaseHistoryPage_clearBtn)
        val container = findViewById<LinearLayout>(R.id.managerPurchaseHistoryPage_container)

        backBtn.setOnClickListener(){
            finish()
        }

        clearBtn.setOnClickListener(){
            storage = ProductPurchaseStorage(this)

            storage.clearAll()  // 저장소 기록 삭제
            container.removeAllViews()  // 화면 리스트 삭제
        }

        //내용을 출력
        showPurchaseHistory(container)



    }

    private fun showPurchaseHistory(container: LinearLayout) {
        container.removeAllViews()

        val storage = ProductPurchaseStorage(this)
        val purchaseList = storage.loadAll()


        Log.d("PurchaseHistory", "Loaded purchase count: ${purchaseList.size}")

        val inflater = LayoutInflater.from(this)
        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)

        for (purchase in purchaseList) {
            val itemView = inflater.inflate(R.layout.item_purchase_history, container, false)

            val date = itemView.findViewById<TextView>(R.id.itemPurchaseHistory_time)
            val nameView = itemView.findViewById<TextView>(R.id.itemPurchaseHistory_name)
            val quantityView = itemView.findViewById<TextView>(R.id.itemPurchaseHistory_num)
            val priceView = itemView.findViewById<TextView>(R.id.itemPurchaseHistory_pirce)

            date.visibility = View.VISIBLE
            date.text = formatter.format(Date(purchase.timestamp))
            nameView.text = purchase.name
            quantityView.text = "${purchase.quantity}개"
            priceView.text = numberFormat.format(purchase.totalPrice) + "원"

            container.addView(itemView)
        }
    }
}