package com.example.lojadehardware_alpha

data class OrderRequest(
    val userId: Int,
    val total: Double,
    val products: List<Produto>,
    val addressId: Int
)