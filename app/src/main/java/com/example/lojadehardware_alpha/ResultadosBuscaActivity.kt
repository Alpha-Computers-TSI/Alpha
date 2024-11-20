package com.example.lojadehardware_alpha

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultadosBuscaActivity : BaseSearchActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resultados_busca)

        // Define a cor de fundo da barra de status
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        recyclerView = findViewById(R.id.recyclerViewResultadosBusca)
        searchResultsMessage = findViewById(R.id.searchResultsMessage)
        searchView = findViewById(R.id.search_view)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CustomAdapter(emptyList())
        recyclerView.adapter = adapter

        termoBusca = intent.getStringExtra("TEXTO_BUSCA") ?: ""
        title = "Resultados para \"$termoBusca\""

        configurarSearchView(searchView) { query ->
            termoBusca = query
            buscarProdutos()
        }

        // Configura o botão de filtros
        val buttonFilters = findViewById<Button>(R.id.button_popular)
        configurarButtonFiltros(buttonFilters) {
            buscarProdutos() // Atualiza os produtos com base no filtro
        }

        buscarProdutos()
    }

    private fun configurarSearchView() {
        // Abre o SearchView ao clicar
        searchView.setOnClickListener {
            searchView.isIconified = false
        }

        // Lida com a submissão da nova busca
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    termoBusca = query // Atualiza o termo de busca
                    title = "Resultados para \"$termoBusca\""
                    buscarProdutos() // Realiza a nova busca
                    fecharTeclado() // Fecha o teclado
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun buscarProdutos() {
        val filtro = filtroSelecionado ?: ""

        apiService.buscarProduto(termoBusca, filtro).enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    if (produtos.isNotEmpty()) {
                        adapter.atualizarLista(produtos)
                        searchResultsMessage.visibility = View.GONE
                    } else {
                        adapter.atualizarLista(emptyList())
                        searchResultsMessage.text = "Nenhum produto encontrado."
                        searchResultsMessage.visibility = View.VISIBLE
                    }
                } else {
                    searchResultsMessage.text = "Erro ao buscar produtos."
                    searchResultsMessage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                searchResultsMessage.text = "Erro ao buscar produtos."
                searchResultsMessage.visibility = View.VISIBLE
            }
        })
    }
}