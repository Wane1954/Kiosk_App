package com.example.kiosk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class ConsumerMainPage : AppCompatActivity(){

    //총 합계
    var totalPrice : Int = 0
    lateinit var totalView : TextView
    var selectedTypeCount : Int = 0

    //모든 제품
    lateinit var productList : List<Product>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.consumer_main_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.consumerMainPage)) { v, insets ->
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

        
        //메인 부분
        val backBtn = findViewById<Button>(R.id.consumerMainPage_backBtn)
        val purchaseBtn =  findViewById<Button>(R.id.consumerMainPage_purchaseBtn)
        val clearBtn = findViewById<Button>(R.id.consumerMainPage_clearBtn)

        totalView = findViewById<TextView>(R.id.consumerMainPage_totalText)

        //돌아가기 버튼
        backBtn.setOnClickListener(){
            finish()
        }
        
        //비우기 버튼
        clearBtn.setOnClickListener(){
            for (product in productList) {
                product.seleacted = 0
            }
            totalPrice = 0
            totalView.text = "0원"
            refreshProductList()
        }

        //구매 버튼
        purchaseBtn.setOnClickListener() {

            // 구매 기록 저장소 생성
            val purchaseStorage = ProductPurchaseStorage(this) // 구매 기록 저장소
            val productStorage = ProductStorage(this)  // 물품별 관리 저장소

            for (product in productList) {
                if (product.seleacted > 0) {
                    val record = ProductPurchase(
                        product.name,
                        product.seleacted,
                        product.price * product.seleacted,
                        System.currentTimeMillis()
                    )
                    purchaseStorage.addPurchase(record)

                    //선택된 숫자를 기억
                    selectedTypeCount++
                    
                    // 구매 후 재고 반영 및 초기화
                    product.inStock -= product.seleacted
                    product.seleacted = 0

                    //변경된 상품 저장
                    productStorage.saveProduct(product)
                }


            }

            val intent = Intent(this, ConsumerPurchasePage::class.java)
            intent.putExtra("selectedTypeCount", selectedTypeCount)
            intent.putExtra("totalPrice", totalPrice)
            startActivity(intent)

            selectedTypeCount = 0
            totalPrice = 0
            totalView.text = "0원"
            refreshProductList()
        }
    }

    //화면으로 돌아올 때마다 호출되는 함수
    override fun onResume() {
        super.onResume()
        refreshProductList()
    }

    // 목록 새로고침 함수
    fun refreshProductList(){

        val container = findViewById<GridLayout>(R.id.cosumerMainPage_productContainer)
        container.removeAllViews() //생성된 목록 삭제

        val inflater = layoutInflater

        productList = ProductStorage(this).getAllProducts()
        for (product in productList) {
            val itemView = inflater.inflate(R.layout.consumer_item_product, container, false)

            val imgView = itemView.findViewById<ImageView>(R.id.consumer_item_img)
            val nameView = itemView.findViewById<TextView>(R.id.consumer_item_name)
            val priceView = itemView.findViewById<TextView>(R.id.consumer_item_price)
            val countView = itemView.findViewById<TextView>(R.id.consumer_item_count)
            val stockoutView = itemView.findViewById<TextView>(R.id.consumer_item_stockoutAlert)

            nameView.setText(product.name)


            val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
            val formattedPrice = numberFormat.format(product.price) + "원"
            priceView.setText(formattedPrice)

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

            //재고가 없는 물품은 미리 재고소진 표시
            if(product.inStock <= 0){
                stockoutView.visibility = View.VISIBLE
            }

            Log.d("ImagePath", "Loading image path: ${product.imgPath}")

            //클릭 리스너 추가
            itemView.setOnClickListener {

                if(product.seleacted == 0){
                    countView.visibility = View.INVISIBLE
                }

                val isCanBuy = product.seleacted < product.inStock

                if(isCanBuy){
                    stockoutView.visibility = View.INVISIBLE

                    totalPrice += product.price

                    product.seleacted++

                    countView.setText(product.seleacted.toString())
                    countView.visibility = View.VISIBLE

                    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
                    val formattedPrice = numberFormat.format(totalPrice) + "원"
                    totalView.setText(formattedPrice)
                }

                else{
                    stockoutView.visibility = View.VISIBLE
                    product.seleacted = product.inStock
                }
                /*val intent = Intent(this, ManagerEditPage::class.java)
                intent.putExtra("product", product)  // Serializable 전달
                startActivity(intent)*/
            }

            container.addView(itemView)
        }
    }
}