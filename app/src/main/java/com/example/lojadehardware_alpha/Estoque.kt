package com.example.lojadehardware_alpha

import com.google.gson.annotations.SerializedName

data class Estoque(
    @SerializedName("PRODUTO_ID") val produtoId: Int,
    @SerializedName("ESTOQUE_QTD") val quantidade: Int
)
