package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lojadehardware_alpha.util.MenuFiltrosHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class BaseSearchActivity : AppCompatActivity() {

    protected lateinit var progressBar: ProgressBar
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var adapter: CustomAdapter
    protected lateinit var searchView: SearchView
    protected lateinit var apiService: ApiService
    protected lateinit var searchResultsMessage: TextView
    protected var termoBusca: String = ""
    protected var filtroSelecionado: String? = null
    protected lateinit var menuFiltrosHelper: MenuFiltrosHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = createRetrofitService("https://2c87926d-7bca-4d8a-b846-4ddddb31c316-00-1y6vahvqnlnmn.worf.replit.dev/")
    }

    protected fun configurarSearchView(searchView: SearchView, onSearch: (String) -> Unit) {
        searchView.setOnClickListener {
            searchView.isIconified = false
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    termoBusca = it
                    onSearch(it)
                    fecharTeclado()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    protected fun configurarButtonFiltros(button: Button, onFiltroSelecionado: () -> Unit) {
        menuFiltrosHelper = MenuFiltrosHelper(this, button) { filtro ->
            filtroSelecionado = filtro
            onFiltroSelecionado()
        }

        button.setOnClickListener { view ->
            menuFiltrosHelper.mostrarMenuFiltros(view)
        }
    }

    protected fun fecharTeclado() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = this.currentFocus
        imm.hideSoftInputFromWindow(view?.windowToken ?: searchView.windowToken, 0)
    }

    protected fun createRetrofitService(baseUrl: String): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}