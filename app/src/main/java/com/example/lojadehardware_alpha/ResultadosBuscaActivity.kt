package com.example.lojadehardware_alpha

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    companion object {
        private const val REQUEST_CODE_FILTROS = 1
    }

    private var filtroDesconto: Boolean? = null
    private var filtroEstoque: Boolean? = null
    private var precoMin: Float? = null
    private var precoMax: Float? = null

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

        progressBar = findViewById(R.id.progressBar)


        // Configura o botão de filtros
        val buttonPopular = findViewById<Button>(R.id.button_popular)
        configurarButtonFiltros(buttonPopular) {
            buscarProdutos()
        }

        val buttonFilters = findViewById<Button>(R.id.button_filters)
        buttonFilters.setOnClickListener {
            Log.d("FiltrosButton", "Criando Intent para FiltrosActivity")
            val intent = Intent(this, FiltrosActivity::class.java).apply {
                putExtra("FILTRO_DESCONTO", filtroDesconto ?: false)
                putExtra("FILTRO_ESTOQUE", filtroEstoque ?: false)
                putExtra("FILTRO_PRECO_MIN", precoMin ?: 0f)
                putExtra("FILTRO_PRECO_MAX", precoMax ?: 5000f)
            }
            startActivityForResult(intent, REQUEST_CODE_FILTROS)
            Log.d("FiltrosButton", "Intent enviado para FiltrosActivity")
        }

        buscarProdutos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILTROS && resultCode == Activity.RESULT_OK) {
            filtroDesconto = data?.getBooleanExtra("FILTRO_DESCONTO", false)
            filtroEstoque = data?.getBooleanExtra("FILTRO_ESTOQUE", false)
            precoMin = data?.getFloatExtra("FILTRO_PRECO_MIN", 0f)
            precoMax = data?.getFloatExtra("FILTRO_PRECO_MAX", 5000f)

            buscarProdutos()
        }
    }

    private fun configurarSearchView() {
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
        progressBar.visibility = View.VISIBLE

        val filtro = filtroSelecionado ?: ""

        apiService.buscarProduto(termoBusca, filtro, filtroDesconto ?: false, filtroEstoque ?: false, precoMin, precoMax )
            .enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                progressBar.visibility = View.GONE

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