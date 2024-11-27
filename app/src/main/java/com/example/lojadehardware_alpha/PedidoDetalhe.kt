package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Button
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
    private lateinit var goToMyAccount: Button
    private lateinit var progressBar: ProgressBar

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://2c87926d-7bca-4d8a-b846-4ddddb31c316-00-1y6vahvqnlnmn.worf.replit.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService = retrofit.create(UserService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_detalhe)

        //Volta para os Dados da Conta
        goToMyAccount = findViewById(R.id.goToMyAccount)
        goToMyAccount.setOnClickListener{
            val intent = Intent(this, MyAccount::class.java)
            startActivity(intent)
            finish()
        }

        // Marcar a aba atual como selecionada
        bottomNavigationView.selectedItemId = R.id.nav_account

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val pedidoId = intent.getIntExtra("PEDIDO_ID", -1)

        if (pedidoId == -1) {
            Toast.makeText(this, "Pedido ID inválido.", Toast.LENGTH_SHORT).show()
            finish()
        }

        carregarItensDoPedido(pedidoId)

    }

    private fun carregarItensDoPedido(pedidoId: Int) {
        progressBar.visibility = ProgressBar.VISIBLE
        userService.listPedidoItens(pedidoId).enqueue(object : Callback<List<PedidoItem>> {
            override fun onResponse(
                call: Call<List<PedidoItem>>,
                response: Response<List<PedidoItem>>

            ) {
                if (response.isSuccessful && response.body() != null) {
                    progressBar.visibility = ProgressBar.GONE
                    val itens = response.body()!!
                    Log.d("PedidoDetalhe", "Itens recebidos: $itens")
                    adapter = PedidoDetalheAdapter(itens)
                    recyclerView.adapter = adapter
                } else {
                    progressBar.visibility = ProgressBar.GONE
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


