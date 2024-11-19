package com.example.lojadehardware_alpha

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lojadehardware_alpha.util.MenuFiltrosHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class ListaProdutos : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter
    private lateinit var tvNenhumProduto: TextView
    private lateinit var menuFiltrosHelper: MenuFiltrosHelper


    private var produtosOriginais: List<Produto> = listOf() // Lista original de produtos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_produtos)

        // Inicializa os componentes
        tvNenhumProduto = findViewById(R.id.tvNenhumProduto)
        recyclerView = findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa o adapter
        adapter = CustomAdapter(emptyList()) // Inicializa com uma lista vazia
        recyclerView.adapter = adapter

        // Configura Retrofit
        val apiServiceListar = createRetrofitService("http://thyagoquintas.com.br/ALPHA/")
        val apiServiceBusca = createRetrofitService("https://027c2e5f-4e20-4907-8ddb-002cce23454a-00-2bk0k8130zh8s.kirk.replit.dev/")

        // Configura a barra de pesquisa
        setupSearchView(apiServiceBusca)

        // Inicializa o MenuFiltrosHelper
        val buttonFilters = findViewById<Button>(R.id.button_popular)
        menuFiltrosHelper = MenuFiltrosHelper(this, buttonFilters)

        buttonFilters.setOnClickListener { view ->
            menuFiltrosHelper.mostrarMenuFiltros(view)
        }

        // Carrega produtos da API
        apiServiceListar.getProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    produtosOriginais = response.body() ?: emptyList()
                    adapter.atualizarLista(produtosOriginais) // Atualiza a lista do adapter
                    tvNenhumProduto.visibility = View.GONE // Oculta mensagem, se existir
                } else {
                    Log.e("API Error", "Response not successful. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Log.e("API Failure", "Error fetching products", t)
            }
        })
    }

    // Função para configurar a barra de pesquisa
    private fun setupSearchView(apiServiceBusca: ApiService) {
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setOnClickListener {
            searchView.isIconified = false
        }


        // Escuta o texto da pesquisa
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarProdutos(newText, apiServiceBusca)
                return true
            }
        })
    }

    // Função para filtrar produtos com base na pesquisa
    private fun filtrarProdutos(query: String?, apiServiceBusca: ApiService) {
        if (!query.isNullOrEmpty()) {
            apiServiceBusca.buscarProduto(query).enqueue(object : Callback<List<Produto>> {
                override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                    if (response.isSuccessful) {
                        val produtosFiltrados = response.body() ?: emptyList()

                        if (produtosFiltrados.isNotEmpty()) {
                            adapter.atualizarLista(produtosFiltrados)
                            tvNenhumProduto.visibility = View.GONE // Oculta mensagem se houver produtos
                        } else {
                            Log.d("Pesquisa", "Nenhum produto encontrado para a pesquisa: $query")
                            tvNenhumProduto.visibility = View.VISIBLE // Exibe a mensagem
                            adapter.atualizarLista(emptyList())
                        }
                    } else {
                        Log.e("API Error", "Response not successful. Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                    Log.e("API Failure", "Error fetching filtered products", t)
                }
            })
        } else {
            adapter.atualizarLista(produtosOriginais) // Restaura a lista original
            tvNenhumProduto.visibility = View.GONE // Oculta a mensagem
        }
    }

    // Função para criar o serviço Retrofit
    private fun createRetrofitService(baseUrl: String): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }


}

