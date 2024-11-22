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
        @Query("filter") filter: String,
        @Query("comDesconto") comDesconto: Boolean,
        @Query("emEstoque") emEstoque: Boolean,
        @Query("precoMin") precoMin: Float?,
        @Query("precoMax") precoMax: Float?
    ): Call<List<Produto>>

    @GET("categoria.php")
    fun getCategorias(): Call<List<Categoria>>

    @GET("produtos_por_categoria.php")
    fun getProdutosPorCategoria(
        @Query("categoriaId") categoriaId: Int
    ): Call<List<Produto>>
}