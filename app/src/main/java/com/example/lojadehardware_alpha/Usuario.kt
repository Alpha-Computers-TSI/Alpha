package com.example.lojadehardware_alpha

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("nome") val nome: String,
    @SerializedName("cpf") val cpf: String,
    @SerializedName("email") val email: String
)

