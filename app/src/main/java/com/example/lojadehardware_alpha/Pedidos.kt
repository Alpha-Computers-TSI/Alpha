package com.example.lojadehardware_alpha

import java.sql.Date

data class Pedidos (
    val PEDIDO_ID: Int,
    val ENDERECO_ID: Int,
    val USUARIO_ID: Int,
    val STATUS_ID: Int,
    val PEDIDO_DATA: Date
)