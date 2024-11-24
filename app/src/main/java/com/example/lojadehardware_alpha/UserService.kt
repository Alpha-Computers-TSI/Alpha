package com.example.lojadehardware_alpha

import Pedidos
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    // Busca um usuário pelo ID via GET
    @GET("usuario.php")
    fun getUsuario(@Query("id") id: Int): Call<Usuario>

    // Atualiza os dados de um usuário pelo ID via POST
    @POST("usuario.php")
    fun updateUser(@Query("id") id: Int, @Body usuario: Usuario): Call<ResponseBody>

    // Lista todos os endereços
    @GET("endereco.php")
    fun listEnderecos(@Query("id") id: Int): Call<Endereco>

    // Atualiza os dados de um endereço específico
    @POST("endereco.php")
    fun updateEndereco(
        @Query("id") id: Int,
        @Body endereco: Endereco
    ): Call<ResponseBody>

    @GET("pedidos.php")
    fun listPedidos(@Query("id") id: Int): Call<List<Pedidos>>

    @GET("pedidoDetalhe.php")
    fun listPedidoItens(@Query("id") pedidoId: Int): Call<List<PedidoItem>>
}