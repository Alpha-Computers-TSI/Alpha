package com.example.lojadehardware_alpha

import com.google.gson.annotations.SerializedName

data class Endereco(
    @SerializedName("ENDERECO_ID") val enderecoId: Int?,
    @SerializedName("ENDERECO_LOGRADOURO") val enderecoLogradouro: String?,
    @SerializedName("ENDERECO_NUMERO") val enderecoNumero: String?,
    @SerializedName("ENDERECO_COMPLEMENTO") val enderecoComplemento: String?,
    @SerializedName("ENDERECO_CIDADE") val enderecoCidade: String?,
    @SerializedName("ENDERECO_ESTADO") val enderecoEstado: String?,
    @SerializedName("ENDERECO_CEP") val enderecoCep: String?
)
