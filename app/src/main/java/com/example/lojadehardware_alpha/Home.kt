package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : BaseSearchActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Configuração da RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CustomAdapter(emptyList())
        recyclerView.adapter = adapter

        // Cor da barra de status
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        // Inicializar ProgressBar
        progressBar = findViewById(R.id.progressBar)

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_categories -> {
                    startActivity(Intent(this, ListaCategorias::class.java))
                    true
                }

                R.id.nav_account -> true
                else -> false
            }
        }

        // Configurar barra de pesquisa
        val searchView = findViewById<SearchView>(R.id.search_view)
        configurarSearchView(searchView) { termo ->
            val intent = Intent(this@Home, ResultadosBuscaActivity::class.java)
            intent.putExtra("TEXTO_BUSCA", termo)
            startActivity(intent)
        }

        carregarProdutos()
    }

    private fun carregarProdutos() {
        progressBar.visibility = View.VISIBLE
        apiService.buscaDescontos().enqueue(object : retrofit2.Callback<List<Produto>> {
            override fun onResponse(
                call: retrofit2.Call<List<Produto>>,
                response: retrofit2.Response<List<Produto>>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    response.body()?.let { produtos ->
                        adapter.atualizarLista(produtos)
                    }
                } else {
                    exibirMensagemErro("Erro ao carregar produtos: ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Produto>>, t: Throwable) {
                progressBar.visibility = View.GONE
                exibirMensagemErro("Falha ao se conectar ao servidor: ${t.message}")
            }
        })
    }

    private fun exibirMensagemErro(mensagem: String) {
        // Exibir uma mensagem como um Toast
        Toast.makeText(this@Home, mensagem, Toast.LENGTH_LONG).show()

        // Ou atualize um TextView específico
        // searchResultsMessage.text = mensagem
    }
}