package com.amigo.analysis

data class ProductEvent(
    val sku: String,
    val name: String,
    val currency: String,
    val price: String
)

data class PurchaseEvent(
    val orderNo: String,
    val sku: String,
    val name: String,
    val price: Double
)
