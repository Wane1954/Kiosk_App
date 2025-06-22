package com.example.kiosk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class ConsumerPurchasePage : AppCompatActivity() {

    lateinit var container : LinearLayout
    lateinit var purchaseStorage : ProductPurchaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.consumer_purchase_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.consumerPurchasePage)) { v, insets ->
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
                systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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

        //결제 정보 불러오기

        val imgView = findViewById<ImageView>(R.id.consumerPurchase_QRImg)
        val addressView = findViewById<TextView>(R.id.consumerPurchase_address)

        val storage = PaymentMethodStorage(this)
        val payment = storage.load()

        payment?.let {
            addressView.setText(it.bankName + " : " +it.accountNumber)

            val qrFile = File(it.qrImagePath)
            if (qrFile.exists()) {
                Glide.with(this)
                    .load(qrFile)
                    .placeholder(R.drawable.qr_sample)
                    .error(R.drawable.qr_sample)
                    .into(imgView)
            } else {
                imgView.setImageResource(R.drawable.qr_sample)
            }
        }



        //Linear 안에 구매기록 불러오기
        val selectedCount = intent.getIntExtra("selectedTypeCount", 0)  // 기본값 0
        val totalPrice = intent.getIntExtra("totalPrice", 0)
        val totalPirceText = findViewById<TextView>(R.id.consumerPurchase_totalText)


        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        val formattedPrice = numberFormat.format(totalPrice) + "원"
        totalPirceText.setText(formattedPrice)
        purchaseStorage = ProductPurchaseStorage(this)
        container = findViewById(R.id.consumerPurchase_container)

        displayPurchaseHistory(selectedCount)


        //메인 부분
        val backBtn = findViewById<Button>(R.id.consumerPurchase_backBtn)

        backBtn.setOnClickListener(){
            finish()
        }

    }

    private fun displayPurchaseHistory(count: Int) {
        container.removeAllViews()

        val recentPurchases = purchaseStorage.getRecent(count)

        val inflater = layoutInflater

        for (purchase in recentPurchases) {
            val itemView = inflater.inflate(R.layout.item_purchase_history, container, false)

            val nameView = itemView.findViewById<TextView>(R.id.itemPurchaseHistory_name)
            val numView = itemView.findViewById<TextView>(R.id.itemPurchaseHistory_num)
            val priceView = itemView.findViewById<TextView>(R.id.itemPurchaseHistory_pirce)

            nameView.text = purchase.name
            numView.text = "${purchase.quantity}개"

            val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
            val formattedPrice = numberFormat.format(purchase.totalPrice) + "원"
            priceView.text = formattedPrice

            container.addView(itemView)
        }
    }
}