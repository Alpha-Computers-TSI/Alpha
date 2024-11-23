package com.example.lojadehardware_alpha

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

}
