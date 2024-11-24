package com.example.lojadehardware_alpha

import com.google.gson.annotations.SerializedName

data class Endereco(
    val ENDERECO_ID: Int? = null,
    val ENDERECO_CEP: String,
    val ENDERECO_LOGRADOURO: String,
    val ENDERECO_NUMERO: String,
    val ENDERECO_NOME: String,
    val ENDERECO_CIDADE: String,
    val ENDERECO_ESTADO: String
)

