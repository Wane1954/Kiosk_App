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

class ManagerPaymentMethodPage: AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imgView: ImageView
    private var selectedImgUri: Uri? = null  // 이미지 경로 저장 변수


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.manager_payment_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.managerPaymentPage)) { v, insets ->
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

        //메인 동작

        val cancelBtn = findViewById<Button>(R.id.managerPaymentPage_cancelBtn)
        val cofirmBtn = findViewById<Button>(R.id.managerPaymentPage_confirmBtn)
        val imgBtn = findViewById<Button>(R.id.managerPaymentPage_imgBtn)

        imgView = findViewById(R.id.managerPaymentPage_img)
        val nameId = findViewById<EditText>(R.id.managerPaymentPage_name)
        val accountId = findViewById<EditText>(R.id.managerPaymentPage_account)

        
        //초기화
        val storage = PaymentMethodStorage(this)
        val payment = storage.load()

        payment?.let {
            nameId.setText(it.bankName)
            accountId.setText(it.accountNumber)

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



        //취소 버튼
        cancelBtn.setOnClickListener(){
            finish()
        }

        //등록 버튼
        cofirmBtn.setOnClickListener(){
            val name: String = nameId.text.toString()   // 은행 이름
            val account: String =  accountId.text.toString() ?: ""     // 계좌 번호
            val imgPath: String = selectedImgUri?.toString() ?: ""

            // 제품 객체 생성
            val paymentMethod = PaymentMethod(
                name,
                account,
                imgPath
            )

            // 저장
            val storage = PaymentMethodStorage(this)
            storage.save(paymentMethod)

            finish()
        }

        //이미지 입력


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
                        .placeholder(R.drawable.qr_sample)
                        .error(R.drawable.qr_sample)
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