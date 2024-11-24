package com.example.lojadehardware_alpha

import Pedidos
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Orders : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrdersAdapter
    private val pedidosList = mutableListOf<Pedidos>()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://eb995d1f-dfff-4a7b-90f7-7ebe2438ad50-00-8qvsbwqugcqv.kirk.replit.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService = retrofit.create(UserService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        // Recuperar o ID do usuário do SharedPreferences
        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", -1)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrdersAdapter(pedidosList)
        recyclerView.adapter = adapter

        fetchPedidos(userId) // Substitua pelo ID real do usuário
    }

    private fun fetchPedidos(userId: Int) {
        userService.listPedidos(userId).enqueue(object : Callback<List<Pedidos>> {
            override fun onResponse(call: Call<List<Pedidos>>, response: Response<List<Pedidos>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        pedidosList.clear()
                        pedidosList.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@Orders, "Erro: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Pedidos>>, t: Throwable) {
                Toast.makeText(this@Orders, "Falha ao conectar à API", Toast.LENGTH_SHORT).show()
                Log.e("OrdersActivity", "Error fetching pedidos", t)
            }
        })
    }
}
