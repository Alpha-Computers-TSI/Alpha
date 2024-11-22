package com.example.lojadehardware_alpha

import android.content.Intent
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


class ListaProdutos : BaseSearchActivity() {

    private lateinit var tvNenhumProduto: TextView
    private var produtosOriginais: List<Produto> = listOf() // Lista original de produtos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_produtos)

        // Inicializa os componentes
        tvNenhumProduto = findViewById(R.id.tvNenhumProduto)
        recyclerView = findViewById(R.id.recyclerViewProdutos)
        searchView = findViewById(R.id.search_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CustomAdapter(emptyList()) // Inicializa o adapter com uma lista vazia
        recyclerView.adapter = adapter

        // Configura a barra de pesquisa
        configurarSearchView(searchView) { query ->
            abrirResultadosBusca(query)
        }

        // Recupera o ID da categoria passada pela intent
        val filtroCategoria = intent.getIntExtra("filtroCategoria", -1)

        // Recupera o nome da categoria passada pela intent
        val nomeCategoria = intent.getStringExtra("nomeCategoria")

        // Atualiza o texto do TextView no FrameLayout
        val textViewInsideView = findViewById<TextView>(R.id.textViewInsideView)
        textViewInsideView.text = nomeCategoria ?: "Categoria"

        // Configurar o MenuFiltrosHelper
        val buttonFilters = findViewById<Button>(R.id.button_popular)
        configurarButtonFiltros(buttonFilters) {
            buscarProdutos() // Recarregar a lista de produtos com o filtro aplicado
        }

        // Carrega produtos com base na categoria selecionada
        carregarProdutos(filtroCategoria)
    }

    private fun carregarProdutos(categoriaId: Int) {
        apiService.getProdutosPorCategoria(categoriaId).enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    if (produtos.isNotEmpty()) {
                        adapter.atualizarLista(produtos)
                        tvNenhumProduto.visibility = View.GONE
                    } else {
                        tvNenhumProduto.text = "Nenhum produto encontrado para esta categoria."
                        tvNenhumProduto.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("API Error", "Erro ao carregar produtos. Código: ${response.code()}")
                    tvNenhumProduto.text = "Erro ao carregar produtos."
                    tvNenhumProduto.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Log.e("API Failure", "Erro ao buscar produtos", t)
                tvNenhumProduto.text = "Erro ao carregar produtos."
                tvNenhumProduto.visibility = View.VISIBLE
            }
        })
    }

    private fun buscarProdutos() {
        val filtro = filtroSelecionado.orEmpty() // Garante que o filtro não seja nulo

        // Aplica o filtro à lista de produtos originais
        val produtosFiltrados = when (filtro) {
            "Preço maior" -> produtosOriginais.sortedByDescending { it.produtoPreco }
            "Preço menor" -> produtosOriginais.sortedBy { it.produtoPreco }
            "Mais recentes" -> produtosOriginais.sortedByDescending { it.produtoId }
            "Mais vendidos" -> produtosOriginais.sortedBy { it.produtudoQtd }
            else -> produtosOriginais // Sem filtro
        }

        // Atualiza o adapter com a lista filtrada
        if (produtosFiltrados.isNotEmpty()) {
            adapter.atualizarLista(produtosFiltrados)
            tvNenhumProduto.visibility = View.GONE
        } else {
            adapter.atualizarLista(emptyList())
            tvNenhumProduto.text = "Nenhum produto encontrado."
            tvNenhumProduto.visibility = View.VISIBLE
        }
    }

    private fun abrirResultadosBusca(query: String) {
        if (query.isNotEmpty()) {
            val intent = Intent(this, ResultadosBuscaActivity::class.java)
            intent.putExtra("TEXTO_BUSCA", query) // Envia o termo da busca para a próxima Activity
            startActivity(intent)
        }
    }
}
