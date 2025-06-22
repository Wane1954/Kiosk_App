package com.example.kiosk


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream

class ManagerEditPage : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imgView: ImageView
    private var selectedImgUri: Uri? = null  // 이미지 경로 저장 변수


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.manager_item_product_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.managerEditPage)) { v, insets ->
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


        //저장부분
        val confirmBtn = findViewById<Button>(R.id.managerEditPage_confirmBtn)
        val cancelBtn = findViewById<Button>(R.id.managerEditPage_cancelBtn)
        val deleteBtn = findViewById<Button>(R.id.managerEditPage_deleteBtn)

        val nameId = findViewById<EditText>(R.id.managerEditPage_name)
        val priceId = findViewById<EditText>(R.id.managerEditPage_price)
        val stockId = findViewById<EditText>(R.id.managerEditPage_stock)
        val inStockId = findViewById<EditText>(R.id.managerEditPage_inStock)


        //수정될 Product를 전달 받아 출력
        val product = intent.getSerializableExtra("product") as? Product

        product?.let {
            findViewById<EditText>(R.id.managerEditPage_name).setText(it.name)
            findViewById<EditText>(R.id.managerEditPage_price).setText(it.price.toString())
            findViewById<EditText>(R.id.managerEditPage_stock).setText(it.stock.toString())
            findViewById<EditText>(R.id.managerEditPage_inStock).setText(it.inStock.toString())

            if(it.imgPath == ""){
                val imageView = findViewById<ImageView>(R.id.managerEditPage_img)
                imageView.setImageResource(R.drawable.no_img)
            }
            else{
                findViewById<ImageView>(R.id.managerEditPage_img).setImageURI(it.imgPath.toUri())
            }
        }

        //저장 버튼 동작
        confirmBtn.setOnClickListener(){
            var id = 0
            if (product != null) {
                id = product.id
            } else {
                id = 0
            }
            val name: String = nameId.text.toString()   // 제품 이름
            val price: Int = priceId.text.toString().toIntOrNull() ?: 0     // 가격
            val stock: Int = stockId.text.toString().toIntOrNull() ?: 0   // 총 재고 수량
            val inStock: Int  = inStockId.text.toString().toIntOrNull() ?: 0   // 현재 남은 수량
            val imgPath: String = selectedImgUri?.toString() ?: product?.imgPath ?: ""

            // 제품 객체 생성
            val product = Product(
                id,
                name,
                price,
                stock,
                inStock,
                imgPath,
                0
            )

            // 저장
            val storage = ProductStorage(this)
            storage.editProduct(product)

            //다시 페이지로 돌아가기
            finish()
        }


        //취소 버튼 동작
        cancelBtn.setOnClickListener(){
            finish()
        }

        //삭제 버튼 동작
        deleteBtn.setOnClickListener(){
            var id = 0
            if (product != null) {
                id = product.id
            } else {
                id = 0
            }
            val storage = ProductStorage(this)
            storage.deleteProduct(id.toString())

            finish()
        }




        //이미지 입력
        val imgBtn = findViewById<Button>(R.id.managerEditPage_imgBtn)
        imgView = findViewById(R.id.managerEditPage_img)

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
            selectedImgUri = data.data

            selectedImgUri?.let { uri ->
                val copiedPath = copyUriToInternalStorage(uri)
                if (copiedPath != null) {
                    // 복사한 파일 경로를 Glide로 로드
                    Glide.with(this)
                        .load(File(copiedPath))
                        .placeholder(R.drawable.no_img)
                        .error(R.drawable.no_img)
                        .into(imgView)

                    // 필요하면 경로를 저장해두고, 서버 저장 시 경로 전달
                    selectedImgUri = copiedPath.toUri()
                } else {
                    // 복사 실패 시 fallback
                    imgView.setImageURI(uri)
                }
            }
        }
    }

    fun copyUriToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
