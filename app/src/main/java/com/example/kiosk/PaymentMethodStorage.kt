package com.example.kiosk

import android.content.Context
import com.google.gson.Gson

class PaymentMethodStorage(context: Context) {
    private val prefs = context.getSharedPreferences("payment_method_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val KEY_PAYMENT_INFO = "payment_method_info"

    // 저장
    fun save(method: PaymentMethod) {
        val json = gson.toJson(method)
        prefs.edit().putString(KEY_PAYMENT_INFO, json).apply()
    }

    // 불러오기
    fun load(): PaymentMethod? {
        val json = prefs.getString(KEY_PAYMENT_INFO, null)
        return json?.let {
            try {
                gson.fromJson(it, PaymentMethod::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    // 삭제
    fun clear() {
        prefs.edit().remove(KEY_PAYMENT_INFO).apply()
    }
}
