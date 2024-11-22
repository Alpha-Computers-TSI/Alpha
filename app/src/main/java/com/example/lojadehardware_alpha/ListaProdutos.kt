package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lojadehardware_alpha.util.MenuFiltrosHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaProdutos : BaseSearchActivity() {

    private lateinit var tvNenhumProduto: TextView
    private var produtosOriginais: List<Produto> = listOf() // Lista original de produtos
    private var filtroDesconto: Boolean? = null
    private var filtroEstoque: Boolean? = null
    private var precoMin: Float? = null
    private var precoMax: Float? = null
    private var filtroOrdenacao: String? = null // Filtro de ordenação

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

        // Recupera os dados intent categoria
        val filtroCategoria = intent.getIntExtra("filtroCategoria", -1)
        val nomeCategoria = intent.getStringExtra("nomeCategoria")

        // Atualiza o texto da categoria na interface
        val textViewInsideView = findViewById<TextView>(R.id.textViewInsideView)
        textViewInsideView.text = nomeCategoria ?: "Categoria"

        // Configura o botão de filtros
        val buttonMenuFiltros = findViewById<Button>(R.id.button_popular)
        configurarButtonFiltrosCategoria(buttonMenuFiltros) {
            carregarOuBuscarProdutos(filtroCategoria, filtroOrdenacao)
        }

        // Carrega produtos da categoria
        carregarOuBuscarProdutos(filtroCategoria)
    }

    private fun configurarButtonFiltrosCategoria(button: Button, onFiltroAplicado: () -> Unit) {
        val helper = MenuFiltrosHelper(this, button) { filtroSelecionado ->
            filtroOrdenacao = when (filtroSelecionado) {
                "Preço maior" -> "maior_preco"
                "Preço menor" -> "menor_preco"
                "Mais recentes" -> "mais_recentes"
                "Mais vendidos" -> "mais_vendidos"
                else -> null
            }
            onFiltroAplicado()
        }

        button.setOnClickListener { helper.mostrarMenuFiltros(it) }
    }

    private fun carregarOuBuscarProdutos(categoriaId: Int, ordem: String? = null) {
        apiService.getProdutosPorCategoria(categoriaId, ordem).enqueue(object : Callback<List<Produto>> {
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

    private fun abrirResultadosBusca(query: String) {
        if (query.isNotEmpty()) {
            val intent = Intent(this, ResultadosBuscaActivity::class.java)
            intent.putExtra("TEXTO_BUSCA", query) // Envia o termo da busca para a próxima Activity
            startActivity(intent)
        }
    }
}
