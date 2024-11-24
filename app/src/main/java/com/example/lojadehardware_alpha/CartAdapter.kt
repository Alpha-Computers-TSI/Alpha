package com.example.lojadehardware_alpha

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CartAdapter(
    private val items: MutableList<Produto>,
    private val context: Context,
    private val updateTotal: () -> Unit,
    private val onQuantityZero: () -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val userId: Int = context.getSharedPreferences("Dados", Context.MODE_PRIVATE)
        .getInt("id", 0)

    // Retrofit centralizado
    private val retrofitService: CartApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://eb995d1f-dfff-4a7b-90f7-7ebe2438ad50-00-8qvsbwqugcqv.kirk.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CartApiService::class.java)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.productNameTextView)
        val productPrice: TextView = view.findViewById(R.id.productPriceTextView)
        val productDiscount: TextView = view.findViewById(R.id.productDiscountTextView)
        val productSubtotal: TextView = view.findViewById(R.id.productSubtotalTextView)
        val productQuantity: TextView = view.findViewById(R.id.quantityTextView)
        val productImage: ImageView = view.findViewById(R.id.productImageView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
        val decreaseButton: ImageButton = view.findViewById(R.id.decreaseButton)
        val increaseButton: ImageButton = view.findViewById(R.id.increaseButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_cart_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val precoFinal = calcularPrecoComDesconto(item.produtoPreco ?: 0.0, item.produtoDesconto)

        holder.apply {
            productName.text = item.produtoNome
            exibirPrecos(holder, item)
            productQuantity.text = item.quantidadeDisponivel.toString()
            productSubtotal.text = String.format("Subtotal: R$%.2f", precoFinal * (item.quantidadeDisponivel ?: 1))

            // Verifica se a URL da imagem é válida ou está vazia
            val imageUrl = if (item.imagemUrl.isNullOrEmpty() || item.imagemUrl == "empty") {
                "https://st4.depositphotos.com/36923632/38547/v/450/depositphotos_385477712-stock-illustration-outline-drug-icon-drug-vector.jpg" // URL padrão
            } else {
                item.imagemUrl
            }

            // Carrega a imagem com Glide
            Glide.with(context).load(imageUrl).into(holder.productImage)

            decreaseButton.setOnClickListener { alterarQuantidade(item, holder, -1) }
            increaseButton.setOnClickListener { alterarQuantidade(item, holder, 1) }
            deleteButton.setOnClickListener { removerItem(item, holder) }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun exibirPrecos(holder: ViewHolder, item: Produto) {
        val preco = item.produtoPreco ?: 0.0
        val desconto = item.produtoDesconto ?: 0.0

        when {
            desconto > 0 -> {
                val precoComDesconto = preco - (preco * (desconto / 100))
                holder.productPrice.apply {
                    text = String.format("R$%.2f", preco)
                    setTextColor(context.getColor(R.color.gray))
                    paintFlags = paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                }
                holder.productDiscount.apply {
                    text = String.format("R$%.2f", precoComDesconto)
                    setTextColor(context.getColor(R.color.Alpha_blue))
                    setTypeface(null, Typeface.BOLD)
                    visibility = View.VISIBLE
                }
            }
            else -> {
                holder.productPrice.apply {
                    text = String.format("R$%.2f", preco)
                    setTextColor(context.getColor(R.color.Alpha_blue))
                    setTypeface(null, Typeface.BOLD)
                    paintFlags = paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                holder.productDiscount.visibility = View.GONE
            }
        }
    }

    private fun alterarQuantidade(item: Produto, holder: ViewHolder, delta: Int) {
        val novaQuantidade = (item.quantidadeDisponivel ?: 0) + delta
        if (novaQuantidade < 0) return

        bloquearBotoes(holder)
        updateProductQuantity(item.produtoId!!, novaQuantidade) { sucesso ->
            if (sucesso) {
                item.quantidadeDisponivel = novaQuantidade
                holder.productQuantity.text = novaQuantidade.toString()
                holder.productSubtotal.text = String.format(
                    "Subtotal: R$%.2f",
                    calcularPrecoComDesconto(item.produtoPreco ?: 0.0, item.produtoDesconto) * novaQuantidade
                )
                updateTotal()
                if (novaQuantidade == 0) onQuantityZero()
            } else {
                Toast.makeText(context, "Erro ao atualizar quantidade", Toast.LENGTH_SHORT).show()
            }
            desbloquearBotoes(holder)
        }
    }

    private fun removerItem(item: Produto, holder: ViewHolder) {
        bloquearBotoes(holder)
        updateProductQuantity(item.produtoId!!, 0) { sucesso ->
            if (sucesso) {
                items.remove(item)
                notifyDataSetChanged()
                onQuantityZero()
            } else {
                desbloquearBotoes(holder)
                Toast.makeText(context, "Erro ao remover item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProductQuantity(produtoId: Int, quantidade: Int, onResult: (Boolean) -> Unit) {
        retrofitService.updateCartQuantity(userId, produtoId, quantidade)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Exibe o toast de sucesso
                        Toast.makeText(context, "Quantidade atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                        onResult(true)
                    } else {
                        // Exibe o toast de falha
                        Toast.makeText(context, "Falha ao atualizar quantidade.", Toast.LENGTH_SHORT).show()
                        onResult(false)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Exibe o toast de falha
                    Toast.makeText(context, "Erro de conexão. Tente novamente.", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            })
    }

    private fun calcularPrecoComDesconto(preco: Double, desconto: Double?): Double =
        desconto?.takeIf { it > 0 }?.let { preco - (preco * (it / 100)) } ?: preco

    private fun bloquearBotoes(holder: ViewHolder) {
        holder.decreaseButton.isEnabled = false
        holder.increaseButton.isEnabled = false
    }

    private fun desbloquearBotoes(holder: ViewHolder) {
        holder.decreaseButton.isEnabled = true
        holder.increaseButton.isEnabled = true

        //Chama o método Delete
        /*private fun removeItemFromCart(item: Produto, position: Int) {
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
        }*/
    }
}