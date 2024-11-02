package com.example.lojadehardware_alpha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductCart : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var goToPaymentButton: Button
    private lateinit var goToListagemProdutos: Button
    private var total: Double = 0.0
    private var productsValue: Double = 0.0
    private lateinit var productsValueTextView: TextView
    private lateinit var parcelamentoTextView: TextView
    private lateinit var cartAdapter: CartAdapter
    private var cartItems: MutableList<Produto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_cart)

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalTextView = findViewById(R.id.totalTextView)
        productsValueTextView = findViewById(R.id.productsValueTextView)
        parcelamentoTextView = findViewById(R.id.parcelamentoTextView)
        goToPaymentButton = findViewById(R.id.goToPaymentButton)
        goToListagemProdutos = findViewById(R.id.goToListagemProdutos)


        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchCartItems()

        goToPaymentButton.setOnClickListener {
            val intent = Intent(this@ProductCart, Payment::class.java)
            startActivity(intent)
        }

        goToListagemProdutos.setOnClickListener {
            val intent = Intent(this@ProductCart, ListaProdutos::class.java)
            startActivity(intent)
        }
    }

    private fun fetchCartItems() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("id", 0)

        val api = retrofit.create(CartApiService::class.java)
        api.getCartItems(userId = idUsuario).enqueue(object : Callback<List<Produto>> {

            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    cartItems = response.body()?.toMutableList() ?: mutableListOf()
                    setupAdapter()
                    updateTotal()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                // Tratamento de erro
            }
        })
    }
    private fun setupAdapter() {
        cartAdapter = CartAdapter(cartItems, this) { updateTotal() }
        recyclerView.adapter = cartAdapter
    }

    private fun updateTotal() {
        // Calcula o total com base nos itens no carrinho com frete
        total = cartItems.sumOf { it.produtoPreco?: 0.0 * (it.quantidadeDisponivel ?: 1) }
        totalTextView.text = "Total: R$${String.format("%.2f", total)}"

        // Calcula o total com base nos itens no carrinho sem frete
        productsValue = cartItems.sumOf { it.produtoPreco?: 0.0 * (it.quantidadeDisponivel ?: 1) }
        productsValueTextView.text = "R$${String.format("%.2f", productsValue)}"

        //Valor do total parcelado
        parcelamentoTextView.text = "ou 12x de R$ ${String.format("%.2f", total / 12)} sem juros!"
    }
}