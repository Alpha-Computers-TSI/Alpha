package com.example.lojadehardware_alpha

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.text.NumberFormat
import java.util.Locale

class SingleProduct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produto_detalhes)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Ocultar a System UI
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Compatibilidade para versões mais antigas
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView)

        // Marcar a aba atual como selecionada
        bottomNavigationView.selectedItemId = 0

        val nomeProduto = intent.getStringExtra("NOME_PRODUTO") ?: "Nome não disponível"
        val descricaoProduto = intent.getStringExtra("DESCRICAO_PRODUTO") ?: "Descrição não disponível"
        val produtoId = intent.getIntExtra("ID_PRODUTO", 0)
        val produtoPreco = intent.getDoubleExtra("PRECO_PRODUTO", 0.0)
        val produtoDesconto = intent.getDoubleExtra("DESCONTO_PRODUTO", 0.0)
        val quantidadeDisponivel = intent.getIntExtra("QUANTIDADE_DISPONIVEL", 0)
        val imagemURL = intent.getStringExtra("IMAGEM_URL") ?: "https://st4.depositphotos.com/36923632/38547/v/450/depositphotos_385477712-stock-illustration-outline-drug-icon-drug-vector.jpg"

        findViewById<TextView>(R.id.txtNomeProduto).text = nomeProduto
        findViewById<TextView>(R.id.txtDescricaoProduto).text = descricaoProduto


        // Define o formato de moeda para o Brasil
        val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        // Formata os valores como moeda
        val precoFormatado = formatoMoeda.format(produtoPreco - (produtoPreco * produtoDesconto) / 100)
        val descontoFormatado = formatoMoeda.format(produtoPreco)

        // Define o texto no TextView de preço
        findViewById<TextView>(R.id.txtPrecoFinal).apply {
            text = precoFormatado
        }

        // Configura o TextView de desconto
        findViewById<TextView>(R.id.txtPrecoOriginal).apply {

            // Efeito texto riscado
            paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

            // Ocultar caso não haja desconto
            if (produtoDesconto == 0.0) {
                visibility = View.GONE // Oculta a view se o desconto for 0
            } else {
                text = descontoFormatado
                visibility = View.VISIBLE // Mostra a view caso tenha um desconto válido
            }

        }

        //findViewById<TextView>(R.id.txtQuantidadeDisponivel).text = quantidadeDisponivel.toString()

        //val editTextQuantidade = findViewById<EditText>(R.id.editQuantidadeDesejada)
        val btnAdicionarCarrinho = findViewById<Button>(R.id.btnAdicionarAoCarrinho)
        val imagemProduto = findViewById<ImageView>(R.id.imagem_produto)

        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        Glide.with(this)
            .load(imagemURL)
            .into(imagemProduto)

        btnAdicionarCarrinho.setOnClickListener {
            val quantidadeDesejada = 1//editTextQuantidade.text.toString().toIntOrNull() ?: 0
            adicionarAoCarrinho(userId, produtoId, quantidadeDesejada)

            val intent = Intent(this@SingleProduct, ProductCart::class.java)
            startActivity(intent)
      }

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Finaliza a Activity atual e retorna à anterior
        }
    }

    // Função para adicionar o produto ao carrinho
    private fun adicionarAoCarrinho(userId: Int, produtoId: Int, quantidade: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/ALPHA/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.adicionarAoCarrinho(userId, produtoId, quantidade).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SingleProduct, response.body() ?: "Sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SingleProduct, "Resposta mal-sucedida", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@SingleProduct, "Erro na API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface ApiService {
        @FormUrlEncoded
        @POST("manter_produto_ao_carrinho/")
        fun adicionarAoCarrinho(
            @Field("userId") userId: Int,
            @Field("produtoId") produtoId: Int,
            @Field("quantidade") quantidade: Int
        ): Call<String>
    }
}