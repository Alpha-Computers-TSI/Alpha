package com.example.lojadehardware_alpha

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PedidoDetalhe : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PedidoDetalheAdapter

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://ca639ef2-1d78-467b-b48a-91e14f4a2f8b-00-37irjmq3m5iwx.spock.replit.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService = retrofit.create(UserService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_detalhe)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val pedidoId = intent.getIntExtra("PEDIDO_ID", -1)
        Log.d("PedidoDetalhe", "Pedido ID recebido: $pedidoId")

        if (pedidoId == -1) {
            Toast.makeText(this, "Pedido ID inválido.", Toast.LENGTH_SHORT).show()
            finish()
        }

        carregarItensDoPedido(pedidoId)

    }

    private fun carregarItensDoPedido(pedidoId: Int) {
        userService.listPedidoItens(pedidoId).enqueue(object : Callback<List<PedidoItem>> {
            override fun onResponse(
                call: Call<List<PedidoItem>>,
                response: Response<List<PedidoItem>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val itens = response.body()!!
                    Log.d("PedidoDetalhe", "Itens recebidos: $itens")
                    adapter = PedidoDetalheAdapter(itens)
                    recyclerView.adapter = adapter
                } else {
                    Log.e("PedidoDetalhe", "Erro na resposta: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(
                        this@PedidoDetalhe,
                        "Erro ao carregar itens.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onFailure(call: Call<List<PedidoItem>>, t: Throwable) {
                Log.e("PedidoDetalhe", "Erro na chamada: ${t.message}", t)
                Toast.makeText(
                    this@PedidoDetalhe,
                    "Erro: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}


