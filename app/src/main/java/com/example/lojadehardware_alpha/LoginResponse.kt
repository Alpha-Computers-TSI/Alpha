package com.example.lojadehardware_alpha

data class LoginResponse(
    val usuarioId: Int,
    val usuarioNome: String,
    val usuarioEmail: String,
    val usuarioSenha: String,
    val usuarioCpf: String
)
