package com.example.lojadehardware_alpha

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ResumoPedidoAdapter(
    private val productList: List<Produto>
) : RecyclerView.Adapter<ResumoPedidoAdapter.ResumoPedidoViewHolder>() {

    // ViewHolder para armazenar as referências às views
    class ResumoPedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.textProductName)
        val productPrice: TextView = itemView.findViewById(R.id.textProductPrice)
        val productDiscount: TextView = itemView.findViewById(R.id.textProductDiscount)
        val productQtd: TextView = itemView.findViewById(R.id.textProductQuantity)
        val productImg: ImageView = itemView.findViewById(R.id.imageProduct)
        val productSubtotal: TextView = itemView.findViewById(R.id.textProductSubtotal)
    }

    // Inflar o layout do item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResumoPedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_cart_summary, parent, false)
        return ResumoPedidoViewHolder(view)
    }

    // Vincular os dados do produto ao ViewHolder
    override fun onBindViewHolder(holder: ResumoPedidoViewHolder, position: Int) {
        val produto = productList[position]

        // Nome do produto
        holder.productName.text = produto.produtoNome

        // Exibir preços
        exibirPrecos(holder, produto)

        // Quantidade de produtos
        val quantidade = produto.quantidadeDisponivel ?: 1 // Supondo que o campo `quantidade` existe
        holder.productQtd.text = "$quantidade"

        // Exibir subtotal
        val precoUnitario = produto.produtoPreco ?: 0.0
        val precoComDesconto = if (produto.produtoDesconto ?: 0.0 > 0) {
            precoUnitario - (precoUnitario * (produto.produtoDesconto!! / 100))
        } else {
            precoUnitario
        }
        val subtotal = precoComDesconto * quantidade
        holder.productSubtotal.text = String.format("Subtotal: R$ %.2f", subtotal)

        // Exibir imagem usando Glide
        Glide.with(holder.itemView.context)
            .load(produto.imagemUrl)
            .into(holder.productImg)
    }

    // Retorna o tamanho da lista
    override fun getItemCount(): Int {
        return productList.size
    }

    // Função para exibir preços com ou sem desconto
    private fun exibirPrecos(holder: ResumoPedidoViewHolder, item: Produto) {
        val preco = item.produtoPreco ?: 0.0
        val desconto = item.produtoDesconto ?: 0.0

        if (desconto > 0) {
            val precoComDesconto = preco - (preco * (desconto / 100))
            holder.productPrice.apply {
                text = String.format("R$ %.2f", preco)
                setTextColor(ContextCompat.getColor(context, R.color.gray))
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            holder.productDiscount.apply {
                text = String.format("R$ %.2f", precoComDesconto)
                setTextColor(ContextCompat.getColor(context, R.color.Alpha_blue))
                setTypeface(null, Typeface.BOLD)
                visibility = View.VISIBLE
            }
        } else {
            holder.productPrice.apply {
                text = String.format("R$ %.2f", preco)
                setTextColor(ContextCompat.getColor(context, R.color.Alpha_blue))
                setTypeface(null, Typeface.BOLD)
                paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            holder.productDiscount.visibility = View.GONE
        }
    }
}
