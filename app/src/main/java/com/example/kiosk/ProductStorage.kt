package com.example.kiosk

import android.content.Context
import com.google.gson.Gson

class ProductStorage(context: Context) {
    private val prefs = context.getSharedPreferences("product_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProduct(product: Product) {
        val key = product.id.toString()  // 제품 id를 키로 사용
        val json = gson.toJson(product)
        prefs.edit().putString(key, json).apply()
    }

    fun editProduct(product: Product, ) {
        saveProduct(product)  // 같은 ID로 덮어쓰기
    }

    fun loadProduct(name: String): Product? {
        val json = prefs.getString(name, null)
        return json?.let { gson.fromJson(it, Product::class.java) }
    }

    fun deleteProduct(id: String) {
        prefs.edit().remove(id).apply()
    }

    fun getAllProducts(): List<Product> {
        return prefs.all.mapNotNull { (_, json) ->
            try {
                gson.fromJson(json as String, Product::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}