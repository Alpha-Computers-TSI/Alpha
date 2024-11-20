package com.example.lojadehardware_alpha

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CartAdapter(private val items: MutableList<Produto>, private val context: Context, private val updateTotal: () -> Unit) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.productNameTextView)
        val productPrice: TextView = view.findViewById(R.id.productPriceTextView)
        val productDiscount: TextView = view.findViewById(R.id.productDiscountTextView)
        val productSubtotal: TextView = view.findViewById(R.id.productSubtotalTextView)
        val productQuantity: TextView = view.findViewById(R.id.productQuantityTextView)
        val productImage: ImageView = view.findViewById(R.id.productImageView)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_cart_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Exibe o nome do produto
        holder.productName.text = item.produtoNome

        // Recupera o preço e o desconto
        val preco = item.produtoPreco ?: 0.0
        val desconto = item.produtoDesconto ?: 0.0

        if (desconto > 0) {
            // Calcula o preço com desconto
            val precoComDesconto = preco - (preco * (desconto / 100))

            // Aplica o estilo para o preço original: cinza e riscado
            holder.productPrice.text = String.format("R$%.2f", preco)
            holder.productPrice.setTextColor(context.getColor(R.color.gray))
            holder.productPrice.paintFlags = holder.productPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG

            // Exibe o preço com desconto: azul
            holder.productDiscount.text = String.format("R$%.2f", precoComDesconto)
            holder.productDiscount.setTextColor(context.getColor(R.color.Alpha_blue))
            holder.productDiscount.setTypeface(null, Typeface.BOLD)

            holder.productDiscount.visibility = View.VISIBLE
        } else {
            // Quando não há desconto, exibe apenas o preço original: azul
            holder.productPrice.text = String.format("R$%.2f", preco)
            holder.productPrice.setTextColor(context.getColor(R.color.Alpha_blue))
            holder.productPrice.setTypeface(null, Typeface.BOLD)
            holder.productPrice.paintFlags = holder.productPrice.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()

            // Esconde o campo de desconto
            holder.productDiscount.visibility = View.GONE
        }

        // Exibe a quantidade disponível
        holder.productQuantity.text = "Qtd: ${item.quantidadeDisponivel ?: 1}"

        // Carrega a imagem do produto com Glide
        Glide.with(context).load(item.imagemUrl).into(holder.productImage)

        // Configura a ação do botão de deletar
        holder.deleteButton.setOnClickListener {
            removeItemFromCart(item, position)
        }
    }


    private fun removeItemFromCart(item: Produto, position: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/ALPHA/carrinho_de_compras/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CartApiService::class.java)


        val sharedPreferences = context.getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        api.deleteCartItem(item.produtoId!!, userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, items.size)
                    updateTotal()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Tratamento de erro ao remover o item
            }
        })
    }

    override fun getItemCount(): Int = items.size
}