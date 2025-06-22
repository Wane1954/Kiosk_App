package com.example.kiosk

import java.io.Serializable

data class Product(
    val id: Int,
    var name: String,
    var price: Int,
    var stock: Int,
    var inStock: Int,
    var imgPath: String,
    var seleacted : Int
) : Serializable
