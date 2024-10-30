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
        viewHolder.valor.text = produto.produtoPreco.toString()

        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        viewHolder.valor.text = numberFormat.format(produto.produtoPreco)


        Glide.with(viewHolder.itemView.context)
            .load(produto.imagemUrl)
            .placeholder(R.drawable.ic_launcher_background) // placeholder
            .error(com.google.android.material.R.drawable.mtrl_ic_error) // indica erro
            .into(viewHolder.imagem)

            val abrirDetalhes = {
                val intent = Intent(viewHolder.itemView.context, SingleProduct::class.java)
                intent.putExtra("NOME_PRODUTO", produto.produtoNome)
                intent.putExtra("DESCRICAO_PRODUTO", produto.produtoDesc)
                intent.putExtra("PRECO_PRODUTO", produto.produtoPreco)
                viewHolder.itemView.context.startActivity(intent)
            }

            // Clique no itemView e no botão de compra para abrir os detalhes
            viewHolder.itemView.setOnClickListener { abrirDetalhes() }
            viewHolder.btnComprar.setOnClickListener { abrirDetalhes() }
        }

        override fun getItemCount(): Int = dataSet.size
    }
