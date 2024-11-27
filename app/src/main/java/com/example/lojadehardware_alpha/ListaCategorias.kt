package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListaCategorias : BaseSearchActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_categorias)

        recyclerView = findViewById(R.id.recyclerViewCategorias)
        searchResultsMessage = findViewById(R.id.searchResultsMessage)
        progressBar = findViewById(R.id.progressBar)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        // Configurar o LayoutManager para o RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configuração da barra de pesquisa
        val searchView = findViewById<SearchView>(R.id.search_view)

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView)

        // Configurar botão para o carrinho
        val cartIcon: ImageView = findViewById(R.id.cart_icon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, ProductCart::class.java)
            startActivity(intent)
        }

        // Ao clicar na barra de pesquisa, ela abre
        searchView.setOnClickListener {
            searchView.isIconified = false
        }

        // Configuração do ouvinte de pesquisa
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    // Passa o termo de busca para a tela de resultados
                    val intent = Intent(this@ListaCategorias, ResultadosBuscaActivity::class.java)
                    intent.putExtra("TEXTO_BUSCA", it)
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Chamar o Retrofit para buscar categorias
        carregarCategorias()
    }

    private fun carregarCategorias() {
        progressBar.visibility = View.VISIBLE

        apiService.getCategorias().enqueue(object : retrofit2.Callback<List<Categoria>> {
            override fun onResponse(call: retrofit2.Call<List<Categoria>>, response: retrofit2.Response<List<Categoria>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val categorias = response.body() ?: emptyList()
                    configurarRecyclerView(categorias)
                } else {
                    mostrarErro("Erro ao carregar categorias")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Categoria>>, t: Throwable) {
                mostrarErro("Erro: ${t.message}")
            }
        })
    }

    private fun configurarRecyclerView(categorias: List<Categoria>) {
        recyclerView.adapter = CategoriaAdapter(categorias) { categoria ->
            // Realizar busca com base na categoria clicada
            iniciarBuscaPorCategoria(categoria)
        }
    }

    private fun iniciarBuscaPorCategoria(categoria: Categoria) {
        // Iniciar a próxima tela (ListaProdutos) com o filtro da categoria
        val intent = Intent(this, ListaProdutos::class.java)
        intent.putExtra("filtroCategoria", categoria.CATEGORIA_ID)
        intent.putExtra("nomeCategoria", categoria.CATEGORIA_NOME)
        startActivity(intent)
    }

    private fun mostrarErro(mensagem: String) {
        searchResultsMessage.text = mensagem
        searchResultsMessage.visibility = View.VISIBLE
    }
}