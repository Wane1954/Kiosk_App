package com.example.kiosk

import java.io.Serializable

data class PaymentMethod(
    val bankName: String,
    val accountNumber: String,
    val qrImagePath: String  // QR코드 이미지 경로
): Serializable