package com.example.lojadehardware_alpha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.util.Locale

class activity_produto_detalhes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_produto_detalhes)

        val nomeProduto = intent.getStringExtra("PRODUTO_NOME")
        val categoriaProduto = intent.getStringExtra("CATEGORIA_ID")
        val descricaoProduto = intent.getStringExtra("PRODUTO_DESC")
        val precoProduto = intent.getStringExtra("PRODUTO_PRECO")
        // Recebe os dados do produto
        val nomeProduto = intent.getStringExtra("NOME_PRODUTO")
        val descricaoProduto = intent.getStringExtra("DESCRICAO_PRODUTO")
        val produtoId = intent.getIntExtra("ID_PRODUTO", 0)
        val quantidadeDesejada = 1

        // Pega o ID do usuário logado do armazenamento local
        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        findViewById<TextView>(R.id.txtNomeProduto).text = nomeProduto
        findViewById<TextView>(R.id.txtCategoriaProduto).text = categoriaProduto
        findViewById<TextView>(R.id.txtDescricaoProduto).text = descricaoProduto

        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        findViewById<TextView>(R.id.txtPrecoProduto).text = numberFormat.format(precoProduto)


        // Adiciona o produto ao carrinho
        findViewById<Button>(R.id.btnAdicionarAoCarrinho).setOnClickListener {
            adicionarAoCarrinho(userId, produtoId, quantidadeDesejada)
            val intent = Intent(this@activity_produto_detalhes,  ProductCart::class.java)
            startActivity(intent)
        }
    }

    // Função para adicionar o produto ao carrinho
    private fun adicionarAoCarrinho(userId: Int, produtoId: Int, quantidade: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/ALPHA/carrinho_de_compras/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        }
    }
}
        val api = retrofit.create(ApiService::class.java)
        api.adicionarAoCarrinho(userId, produtoId, quantidade).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@activity_produto_detalhes, response.body() ?: "Sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@activity_produto_detalhes, "Resposta mal sucedida", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@activity_produto_detalhes, "Erro na API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface ApiService {
        @retrofit2.http.FormUrlEncoded
        @retrofit2.http.POST("getCartItems/")
        fun adicionarAoCarrinho(
            @retrofit2.http.Field("userId") userId: Int,
            @retrofit2.http.Field("produtoId") produtoId: Int,
            @retrofit2.http.Field("quantidade") quantidade: Int
        ): Call<String>
    }
}
