package com.example.lojadehardware_alpha

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductCart : AppCompatActivity() {
    private lateinit var emptyCartLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var productListBtn: Button
    private lateinit var totalTextView: TextView
    private lateinit var goToPaymentButton: Button
    private lateinit var goToListagemProdutos: Button
    private lateinit var productsValueTextView: TextView
    private lateinit var parcelamentoTextView: TextView
    private lateinit var forgottenCepTextView: TextView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var goBackToHomeArrow: ImageView

    private var total: Double = 0.0
    private var productsValue: Double = 0.0

    private var cartItems: MutableList<Produto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_cart)

        emptyCartLayout = findViewById(R.id.emptyCartLayout)
        recyclerView = findViewById(R.id.cartRecyclerView)
        productListBtn = findViewById(R.id.productListBtn)
        totalTextView = findViewById(R.id.totalTextView)
        productsValueTextView = findViewById(R.id.productsValueTextView)
        parcelamentoTextView = findViewById(R.id.parcelamentoTextView)
        goToPaymentButton = findViewById(R.id.goToPaymentButton)
        goToListagemProdutos = findViewById(R.id.goToListagemProdutos)
        forgottenCepTextView = findViewById(R.id.forgottenCepTextView)
        goBackToHomeArrow = findViewById(R.id.goBackToHomeArrow)

        recyclerView.layoutManager = LinearLayoutManager(this)

        //Busca os itens do carrinho
        fetchCartItems()

        goBackToHomeArrow.setOnClickListener{
            finish() // Finaliza a Activity atual e retorna à anterior
        }

        forgottenCepTextView.setOnClickListener {
            val url = "https://buscacepinter.correios.com.br/app/endereco/index.php"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        productListBtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        goToPaymentButton.setOnClickListener {
            // Verifica se há itens no carrinho
            if (cartItems.isEmpty()) {
                // Exibe uma mensagem informando que é preciso adicionar produtos
                Toast.makeText(this, "Adicione produtos ao carrinho antes de prosseguir para o pagamento.", Toast.LENGTH_SHORT).show()
            } else {
                // Se houver produtos, vai para a tela de pagamento
                val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getInt("id", 0)
                val intent = Intent(this, Payment::class.java).apply {
                    putExtra("TOTAL", total.toString())
                    putExtra("USER", userId)
                    putParcelableArrayListExtra("PRODUCT_LIST", ArrayList(cartItems))
                }
                startActivity(intent)
            }
        }


        goToListagemProdutos.setOnClickListener {
            val intent = Intent(this@ProductCart, Home::class.java)
            startActivity(intent)
        }
    }

    private fun fetchCartItems() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://2c87926d-7bca-4d8a-b846-4ddddb31c316-00-1y6vahvqnlnmn.worf.repl.co/")
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

                    // Verifica se o carrinho está vazio e ajusta os layouts
                    if (cartItems.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        emptyCartLayout.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        emptyCartLayout.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                // Tratamento de erro
            }
        })
    }


    private fun setupAdapter() {
        cartAdapter = CartAdapter(cartItems, this, { updateTotal() }, { onQuantityZero() })
        recyclerView.adapter = cartAdapter
    }

    private fun updateTotal() {
        // Calcula o total com base nos itens no carrinho com frete
        total = cartItems.sumOf { item ->
            val precoBase = item.produtoPreco ?: 0.0
            val desconto = item.produtoDesconto ?: 0.0

            val precoFinal = if (desconto > 0) {
                precoBase - (precoBase * (desconto / 100))
            } else {
                precoBase
            }
            precoFinal * (item.quantidadeDisponivel ?: 1)
        }
        totalTextView.text = "Total: R$${String.format("%.2f", total)}"

        // Calcula o total com base nos itens no carrinho sem frete
        productsValue = cartItems.sumOf { item ->
            val precoBase = item.produtoPreco ?: 0.0
            val desconto = item.produtoDesconto ?: 0.0

            val precoFinal = if (desconto > 0) {
                precoBase - (precoBase * (desconto / 100))
            } else {
                precoBase
            }
            precoFinal * (item.quantidadeDisponivel ?: 1)
        }
        productsValueTextView.text = "R$${String.format("%.2f", productsValue)}"

        // Valor do total parcelado
        parcelamentoTextView.text = "ou 12x de R$ ${String.format("%.2f", total / 12)} sem juros!"
    }

    private fun onQuantityZero() {
        fetchCartItems()

    }

}