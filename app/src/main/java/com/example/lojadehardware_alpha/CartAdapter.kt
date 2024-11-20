package com.example.lojadehardware_alpha

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

class CartAdapter(private val items: MutableList<Produto>, private val context: Context, private val updateTotal: () -> Unit, private val onQuantityZero: () -> Unit) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    //Busca o ID do usuário logado para usar em requisições
    val sharedPreferences = context.getSharedPreferences("Dados", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getInt("id", 0)

    /*------------------------------------Captura os elementos do Detalhe que serão inflados-------------------------------*/
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.productNameTextView)
        val productPrice: TextView = view.findViewById(R.id.productPriceTextView)
        val productDiscount: TextView = view.findViewById(R.id.productDiscountTextView)
        val productSubtotal: TextView = view.findViewById(R.id.productSubtotalTextView)
        var productQuantity: TextView = view.findViewById(R.id.quantityTextView)
        val productImage: ImageView = view.findViewById(R.id.productImageView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
        val decreaseButton: ImageButton = view.findViewById(R.id.decreaseButton)
        val increaseButton: ImageButton = view.findViewById(R.id.increaseButton)
    }

    /*-----------------------------------Infla o layout do item do carrinho-----------------------------------------------*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_cart_item, parent, false)
        return ViewHolder(view)
    }

    /*-----------------------------------Trata os dados de cada produto e atribui aos elementos do layout-----------------*/
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

        // Atribui a quantidade disponível do item
        var quantidade = item.quantidadeDisponivel ?: 1

        // Exibe a quantidade no TextView
        holder.productQuantity.text = quantidade.toString()

        // Define o subtotal com base na quantidade
        val precoFinal = calcularPrecoComDesconto(item.produtoPreco ?: 0.0, item.produtoDesconto)
        holder.productSubtotal.text = String.format("Subtotal: R$%.2f", precoFinal * quantidade)

// Lógica para diminuir a quantidade
        holder.decreaseButton.setOnClickListener {
            if (quantidade > 0) {
                bloquearBotoes(holder)
                quantidade-- // Decrementa a variável local
                item.quantidadeDisponivel = quantidade // Atualiza o valor no item

                if (quantidade == 0) {
                    // Chama a função de listagem de produtos no carrinho quando a quantidade é zero
                    onQuantityZero()
                }

                atualizarQuantidadeEExibir(holder, item, userId, quantidade) {
                    desbloquearBotoes(holder)
                }
            }
        }

// Lógica para aumentar a quantidade
        holder.increaseButton.setOnClickListener {
            bloquearBotoes(holder)
            quantidade++ // Incrementa a variável local
            item.quantidadeDisponivel = quantidade // Atualiza o valor no item

            atualizarQuantidadeEExibir(holder, item, userId, quantidade) {
                desbloquearBotoes(holder)
            }
        }


        // Carrega a imagem do produto com Glide
        Glide.with(context).load(item.imagemUrl).into(holder.productImage)

        // Configura a ação do botão de deletar
        holder.deleteButton.setOnClickListener {
            // Desativa o botão temporariamente
            holder.deleteButton.isEnabled = false

            val quantidade = 0 // Reduz a quantidade para zero
            updateProductQuantity(userId, item.produtoId!!, quantidade) { sucesso ->
                if (sucesso) {
                    // Atualiza a interface do RecyclerView
                    onQuantityZero()
                } else {
                    // Em caso de erro, reativa o botão
                    holder.deleteButton.isEnabled = true
                }
            }
        }

    }

    /*--------------------------------Função para atualizar a quantidade do produto no banco----------------------------*/
    fun updateProductQuantity(
        userId: Int,
        produtoId: Int,
        novaQuantidade: Int,
        onResult: (Boolean) -> Unit
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://027c2e5f-4e20-4907-8ddb-002cce23454a-00-2bk0k8130zh8s.kirk.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CartApiService::class.java)

        service.updateCartQuantity(userId, produtoId, novaQuantidade).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Atualização bem-sucedida
                    Toast.makeText(context, "Quantidade atualizada!", Toast.LENGTH_SHORT).show()
                    onResult(true) // Notifica sucesso
                } else {
                    // Falha na atualização
                    Toast.makeText(context, "Falha ao atualizar a quantidade.", Toast.LENGTH_SHORT).show()
                    onResult(false) // Notifica falha
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Erro na comunicação
                Toast.makeText(context, "Erro de comunicação: ${t.message}", Toast.LENGTH_SHORT).show()
                onResult(false) // Notifica falha
            }
        })
    }

    // Verifica se há um desconto válido e aplica o cálculo
    private fun calcularPrecoComDesconto(preco: Double, desconto: Double?): Double {
        return if (desconto != null && desconto > 0) {
            preco - (preco * (desconto / 100))
        } else {
            preco
        }
    }

    private fun atualizarQuantidadeEExibir(
        holder: ViewHolder,
        item: Produto,
        userId: Int,
        quantidade: Int,
        onComplete: () -> Unit
    ) {
        // Atualiza o valor na UI
        holder.productQuantity.text = quantidade.toString()

        // Atualiza o total do carrinho
        updateTotal()

        // Atualiza a quantidade no banco
        updateProductQuantity(userId, item.produtoId!!, quantidade) { sucesso ->
            if (!sucesso) {
                // Em caso de erro, revertemos a exibição para o valor anterior
                Toast.makeText(context, "Erro ao atualizar a quantidade", Toast.LENGTH_SHORT).show()
                onComplete()
                return@updateProductQuantity
            }

            // Calcula o preço com ou sem desconto
            val precoFinal = calcularPrecoComDesconto(item.produtoPreco ?: 0.0, item.produtoDesconto)

            // Atualiza o subtotal
            holder.productSubtotal.text = String.format("Subtotal: R$%.2f", precoFinal * quantidade)

            // Conclui o processo
            onComplete()
        }
    }


    private fun bloquearBotoes(holder: ViewHolder) {
        holder.decreaseButton.isEnabled = false
        holder.increaseButton.isEnabled = false
    }

    private fun desbloquearBotoes(holder: ViewHolder) {
        holder.decreaseButton.isEnabled = true
        holder.increaseButton.isEnabled = true
    }



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

    override fun getItemCount(): Int = items.size
}