package com.example.lojadehardware_alpha

import Pedidos
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Orders : AppCompatActivity() {

    private lateinit var semPedidosImg: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrdersAdapter
    private lateinit var progressBar: ProgressBar
    private val pedidosList = mutableListOf<Pedidos>()
    private lateinit var bntvoltarMyAccont: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://2c87926d-7bca-4d8a-b846-4ddddb31c316-00-1y6vahvqnlnmn.worf.replit.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService = retrofit.create(UserService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView)

        // Recuperar o ID do usuário do SharedPreferences
        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", -1)

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = OrdersAdapter(pedidosList)
        recyclerView.adapter = adapter
        progressBar = findViewById(R.id.progressBar)

        // Referência à imagem que será visível quando não houver pedidos
        semPedidosImg = findViewById(R.id.semPedidosImg)

        // Chamar a função para buscar os pedidos
        fetchPedidos(userId) // Substitua pelo ID real do usuário

        // Configurar o botão de voltar

        bntvoltarMyAccont = findViewById(R.id.bntvoltarMyAccont)
        bntvoltarMyAccont.setOnClickListener {
            val intent = Intent(this, MyAccount::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchPedidos(userId: Int) {
        progressBar.visibility = View.VISIBLE
        userService.listPedidos(userId).enqueue(object : Callback<List<Pedidos>> {
            override fun onResponse(call: Call<List<Pedidos>>, response: Response<List<Pedidos>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    response.body()?.let {
                        pedidosList.clear()
                        pedidosList.addAll(it)
                        adapter.notifyDataSetChanged()

                        // Verificar se a lista de pedidos está vazia
                        if (pedidosList.isEmpty()) {
                            recyclerView.visibility = View.GONE
                            semPedidosImg.visibility = View.VISIBLE
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            semPedidosImg.visibility = View.GONE
                        }
                    }
                } else {
                    Toast.makeText(this@Orders, "Erro: ${response.message()}", Toast.LENGTH_SHORT).show()
                    // Mostrar imagem de sem pedidos em caso de erro na resposta
                    recyclerView.visibility = View.GONE
                    semPedidosImg.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Pedidos>>, t: Throwable) {
                // Mostrar Toast de falha e exibir a imagem
                Toast.makeText(this@Orders, "Falha ao carregar pedidos", Toast.LENGTH_SHORT).show()
                Log.e("OrdersActivity", "Error fetching pedidos", t)

                recyclerView.visibility = View.GONE
                semPedidosImg.visibility = View.VISIBLE
            }
        })
    }
}



