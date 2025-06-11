package com.example.kiosk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ManagerRegisterPage : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imgView: ImageView
    private var selectedImgUri: Uri? = null  // 이미지 경로 저장 변수


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

        val nameId = findViewById<EditText>(R.id.managerRegisterPage_name);
        val priceId = findViewById<EditText>(R.id.managerRegisterPage_price);
        val stockId = findViewById<EditText>(R.id.managerRegisterPage_stock);

        confirmBtn.setOnClickListener(){

            val name: String = nameId.text.toString()   // 제품 이름
            val price: Int = priceId.text.toString().toIntOrNull() ?: 0     // 가격
            val stock: Int = stockId.text.toString().toIntOrNull() ?: 0   // 총 재고 수량
            val inStock: Int  = stockId.text.toString().toIntOrNull() ?: 0   // 현재 남은 수량
            val imgPath: String = selectedImgUri?.toString() ?: ""

            // 제품 객체 생성
            val product = Product(
                name,
                price,
                stock,
                inStock,
                imgPath
            )

            // 저장
            val storage = ProductStorage(this)
            storage.saveProduct(product)

            //다시 페이지로 돌아가기

            val intent = Intent(this,ManagerMainPage::class.java)
            startActivity(intent)
            finish()

            /*// 불러오기
            val loadedCola = storage.loadProduct("콜라")
            if (loadedCola != null) {
                Log.d("불러온 상품", loadedCola.name)
            }*/
        }

        cancelBtn.setOnClickListener(){
            finish()
        }



        //이미지 입력
        val imgBtn = findViewById<Button>(R.id.managerRegisterPage_imgBtn)
        imgView = findViewById(R.id.managerRegisterPage_img)

        imgBtn.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    // 선택된 이미지의 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImgUri = data.data  // URI 저장
            imgView.setImageURI(selectedImgUri)  // 이미지 뷰에 표시
        }
    }
}