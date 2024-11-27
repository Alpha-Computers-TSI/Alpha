package com.example.lojadehardware_alpha

data class PedidoItem(
    val PRODUTO_ID: Int,
    val PRODUTO_NOME: String,
    val PRODUTO_DESC: String,
    val ITEM_PRECO_COM_DESCONTO: Double,
    val ITEM_QTD: Int,
    val IMAGEM_URL: String?
)