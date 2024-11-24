package com.example.lojadehardware_alpha

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CartApiService {
    @GET("getCartItems.php")
    fun getCartItems(@Query("userId") userId: Int): Call<List<Produto>>

    @FormUrlEncoded
    @POST("updateCartQtd.php")
    fun updateCartQuantity(
        @Field("userId") userId: Int,
        @Field("produtoId") produtoId: Int,
        @Field("novaQuantidade") novaQuantidade: Int
    ): Call<ResponseBody>
}