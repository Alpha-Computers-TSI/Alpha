package com.example.lojadehardware_alpha

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("USUARIO_NOME") val USUARIO_NOME: String,
    @SerializedName("USUARIO_CPF") val USUARIO_CPF: String,
    @SerializedName("USUARIO_EMAIL") val USUARIO_EMAIL: String
)

