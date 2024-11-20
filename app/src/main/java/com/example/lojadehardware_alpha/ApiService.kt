package com.example.lojadehardware_alpha

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("lista_de_produtos/")
    fun getProdutos(): Call<List<Produto>>

    @GET("busca.php")
    fun buscarProduto(
        @Query("query") termo: String,
        @Query("filter") filter: String
    ): Call<List<Produto>>
}