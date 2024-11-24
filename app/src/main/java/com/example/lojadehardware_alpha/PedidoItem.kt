package com.example.lojadehardware_alpha

data class PedidoItem(
    val PRODUTO_ID: Int,
    val PRODUTO_NOME: String,
    val PRODUTO_DESC: String,
    val ITEM_PRECO: Double,
    val IMAGEM_URL: String?
)