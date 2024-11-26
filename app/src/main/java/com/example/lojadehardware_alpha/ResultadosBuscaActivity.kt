package com.example.lojadehardware_alpha

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lojadehardware_alpha.util.MenuFiltrosHelper
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
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var apiService: ApiService
    private lateinit var menuFiltrosHelper: MenuFiltrosHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resultados_busca)

        // Define a cor de fundo da barra de status
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        // Inicializa o MenuFiltrosHelper
        val buttonFilters = findViewById<Button>(R.id.button_popular)

        menuFiltrosHelper = MenuFiltrosHelper(this, buttonFilters)

        buttonFilters.setOnClickListener { view ->
            menuFiltrosHelper.mostrarMenuFiltros(view)
        }

        recyclerView = findViewById(R.id.recyclerViewResultadosBusca)
        searchResultsMessage = findViewById(R.id.searchResultsMessage)
        searchView = findViewById(R.id.search_view)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CustomAdapter(emptyList()) // Inicializa com uma lista vazia
        recyclerView.adapter = adapter

        termoBusca = intent.getStringExtra("TEXTO_BUSCA") ?: ""

        title = "Resultados para \"$termoBusca\""

        configurarSearchView()

        apiService = createRetrofitService("https://2c87926d-7bca-4d8a-b846-4ddddb31c316-00-1y6vahvqnlnmn.worf.replit.dev/")
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

    private fun fecharTeclado() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = this.currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } else {
            imm.hideSoftInputFromWindow(searchView.windowToken, 0)
        }
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
                        adapter.atualizarLista(emptyList())
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