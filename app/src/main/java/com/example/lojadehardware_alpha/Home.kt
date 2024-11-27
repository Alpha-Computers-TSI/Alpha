package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Handler
import android.widget.ImageView


class Home : BaseSearchActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bannerAdapter: BannerAdapter
    private val bannerImages = listOf(R.drawable.banner_modelo, R.drawable.banner_modelo2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Configurar o ViewPager2
        viewPager = findViewById(R.id.viewPagerBanner)

        // Configurar o Adapter
        bannerAdapter = BannerAdapter(this, bannerImages)
        viewPager.adapter = bannerAdapter



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
        BottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView)

        // Configurar botão para o carrinho
        val cartIcon: ImageView = findViewById(R.id.cart_icon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, ProductCart::class.java)
            startActivity(intent)
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