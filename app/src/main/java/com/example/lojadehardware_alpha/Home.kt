package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Define a cor de fundo da barra de status
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)


        // Pesquisa de produto
        val searchView = findViewById<SearchView>(R.id.search_view)

        //ao clicar em qualquer parte da barra de pesquisa ela abre
        searchView.setOnClickListener {
            searchView.isIconified = false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val intent = Intent(this@Home, ResultadosBuscaActivity::class.java)
                    intent.putExtra("TEXTO_BUSCA", it)
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

            bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_categories -> {
                    startActivity(Intent(this, ListaCategorias::class.java))
                    true
                }
                R.id.nav_account -> {
                    //startActivity(Intent(this, AccountActivity::class.java))
                    true
                }
                else -> false
            }
        }
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }
}