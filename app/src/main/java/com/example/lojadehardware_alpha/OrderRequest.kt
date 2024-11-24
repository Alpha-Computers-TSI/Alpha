package com.example.lojadehardware_alpha

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("total") val total: Double,
    @SerializedName("products") val products: List<Produto>,
    @SerializedName("addressId") val addressId: Int
)
