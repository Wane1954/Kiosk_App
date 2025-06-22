package com.example.kiosk

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

data class ProductPurchase(
    val name: String,
    val quantity: Int,
    val totalPrice: Int,
    val timestamp: Long
) : Serializable

class ProductPurchaseStorage(context: Context) {
    private val prefs = context.getSharedPreferences("product_purchase_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val key = "product_purchase_records"

    // 저장된 JSON 배열을 리스트로 불러오기
    fun loadAll(): MutableList<ProductPurchase> {
        val json = prefs.getString(key, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<ProductPurchase>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    // 새로운 구매 기록 추가 후 저장
    fun addPurchase(purchase: ProductPurchase) {
        val list = loadAll()
        list.add(purchase)
        val json = gson.toJson(list)
        prefs.edit().putString(key, json).apply()
    }

    // 기록 모두 삭제
    fun clearAll() {
        prefs.edit().remove(key).apply()
    }

    // 최근 n개의 구매 기록을 반환 (최신순)
    fun getRecent(count: Int): List<ProductPurchase> {
        val allRecords = loadAll()
        return allRecords.asReversed().take(count)
    }
}
