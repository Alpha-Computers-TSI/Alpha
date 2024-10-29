package com.example.lojadehardware_alpha

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale
import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class CustomAdapter(private val dataSet: List<Produto>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nomeProduto)
        val valor: TextView = view.findViewById(R.id.valorProduto)
        val imagem: ImageView = view.findViewById(R.id.imagem_produto)
        val btnComprar: Button = view.findViewById(R.id.btnComprar)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_produto, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val produto = dataSet[position]
        viewHolder.nome.text = produto.produtoNome

        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        viewHolder.valor.text = numberFormat.format(produto.produtoPreco)

        Glide.with(viewHolder.itemView.context)
            .load(produto.imagemUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(com.google.android.material.R.drawable.mtrl_ic_error)
            .into(viewHolder.imagem)

        // Adiciona ao carrinho
        viewHolder.btnComprar.setOnClickListener {
          /*  val userId = viewHolder.itemView.context.getSharedPreferences("Dados", Context.MODE_PRIVATE).getInt("id", 0)
            adicionarAoCarrinho(userId, produto.produtoId, 1, viewHolder.itemView.context)*/

            // Muda para a tela do carrinho após adicionar o item
            val intent = Intent(viewHolder.itemView.context, ProductCart::class.java)
            viewHolder.itemView.context.startActivity(intent)
        }

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(viewHolder.itemView.context, SingleProduct::class.java)
            intent.putExtra("PRODUTO_NOME", produto.produtoNome)
            intent.putExtra("PRODUTO_DESC", produto.produtoDesc)
            intent.putExtra("CATEGORIA_ID", produto.categoriaId)
            intent.putExtra("PRODUTO_PRECO", produto.produtoPreco)
            viewHolder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataSet.size

    private fun adicionarAoCarrinho(userId: Int, produtoId: Int, quantidade: Int, context: Context) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/ALPHA/carrinho_de_compras/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.adicionarAoCarrinho(userId, produtoId, quantidade).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, response.body() ?: "Sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Resposta mal sucedida", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context, "Erro na API: ${t.message}", Toast.LENGTH_SHORT).show()
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
