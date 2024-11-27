package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lojadehardware_alpha.util.MenuFiltrosHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaProdutos : BaseSearchActivity() {

    companion object {
        const val REQUEST_CODE_FILTROS = 1
    }

    private lateinit var tvNenhumProduto: TextView
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

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView)

        // Configurar botão para o carrinho
        val cartIcon: ImageView = findViewById(R.id.cart_icon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, ProductCart::class.java)
            startActivity(intent)
        }

        progressBar = findViewById(R.id.progressBar)


        // Recupera os dados da intent de categoria
        val filtroCategoria = intent.getIntExtra("filtroCategoria", -1)
        val nomeCategoria = intent.getStringExtra("nomeCategoria")

        // Atualiza o texto da categoria na interface
        val textViewInsideView = findViewById<TextView>(R.id.textViewInsideView)
        textViewInsideView.text = nomeCategoria ?: "Categoria"

        // Configura o botão de filtros de ordenação
        val buttonMenuFiltros = findViewById<Button>(R.id.button_popular)
        configurarButtonFiltrosCategoria(buttonMenuFiltros) {
            carregarOuBuscarProdutos(filtroCategoria, filtroOrdenacao)
        }

        // Configura o botão para abrir os filtros avançados
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

        // Carrega os produtos iniciais com base na categoria
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
        progressBar.visibility = View.VISIBLE

        apiService.getProdutosPorCategoria(
            categoriaId = categoriaId,
            ordem = ordem,
            filtroDesconto ?: false, filtroEstoque ?: false,
            precoMin = precoMin,
            precoMax = precoMax
        ).enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    if (produtos.isNotEmpty()) {
                        adapter.atualizarLista(produtos)
                        tvNenhumProduto.visibility = View.GONE
                    } else {
                        adapter.atualizarLista(emptyList())
                        tvNenhumProduto.text = "Nenhum produto encontrado para esta categoria."
                        tvNenhumProduto.visibility = View.VISIBLE
                    }
                } else {
                    adapter.atualizarLista(emptyList())
                    Log.e("API Error", "Erro ao carregar produtos. Código: ${response.code()}")
                    tvNenhumProduto.text = "Erro ao carregar produtos."
                    tvNenhumProduto.visibility = View.VISIBLE
                }
            }
            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                adapter.atualizarLista(emptyList())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILTROS && resultCode == RESULT_OK) {
            filtroDesconto = data?.getBooleanExtra("FILTRO_DESCONTO", false)
            filtroEstoque = data?.getBooleanExtra("FILTRO_ESTOQUE", false)
            precoMin = data?.getFloatExtra("FILTRO_PRECO_MIN", 0f)
            precoMax = data?.getFloatExtra("FILTRO_PRECO_MAX", 5000f)

            // Recarrega os produtos com os novos filtros
            val filtroCategoria = intent.getIntExtra("filtroCategoria", -1)
            carregarOuBuscarProdutos(filtroCategoria, filtroOrdenacao)
        }
    }
}