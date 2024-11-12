package com.example.lojadehardware_alpha

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResultadosBuscaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter
    private lateinit var termoBusca: String
    private lateinit var searchResultsMessage: TextView
    private lateinit var apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resultados_busca)

        recyclerView = findViewById(R.id.recyclerViewResultadosBusca)
        searchResultsMessage = findViewById(R.id.searchResultsMessage)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CustomAdapter(emptyList()) // Inicializa com uma lista vazia
        recyclerView.adapter = adapter

        termoBusca = intent.getStringExtra("TEXTO_BUSCA") ?: ""

        title = "Resultados para \"$termoBusca\""

        apiService = createRetrofitService("https://027c2e5f-4e20-4907-8ddb-002cce23454a-00-2bk0k8130zh8s.kirk.replit.dev/")
        buscarProdutos()
    }

    private fun buscarProdutos() {
        apiService.buscarProduto(termoBusca).enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()

                    // Verifica se há produtos e exibe mensagem caso contrário
                    if (produtos.isNotEmpty()) {
                        Log.d("API Response", "Produtos encontrados: ${produtos.size}")
                        adapter.atualizarLista(produtos)
                        searchResultsMessage.visibility = View.GONE
                    } else {
                        Log.d("API Response", "Nenhum produto encontrado para a busca: $termoBusca")
                        Log.d("Busca", "Buscando produtos com termo: '$termoBusca'")

                        searchResultsMessage.text = "Nenhum produto encontrado."
                        searchResultsMessage.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("API Error", "Response not successful. Code: ${response.code()}")
                    searchResultsMessage.text = "Erro ao buscar produtos."
                    searchResultsMessage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Log.e("API Failure", "Erro ao buscar produtos", t)
                searchResultsMessage.text = "Erro ao buscar produtos."
                searchResultsMessage.visibility = View.VISIBLE
            }
        })
    }

    // Função para criar o serviço Retrofit com a URL fornecida
    private fun createRetrofitService(baseUrl: String): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}