package com.example.lojadehardware_alpha

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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

class CustomAdapter(private var dataSet: List<Produto>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nomeProduto)
        val valor: TextView = view.findViewById(R.id.valorProduto)
        val desconto: TextView = view.findViewById(R.id.descontoProduto)
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

        // Formata o valor original do produto
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        val precoFormatado = numberFormat.format(produto.produtoPreco)

        // Calcula o valor com desconto
        val descontoPorcentagem = produto.produtoDesconto.toDoubleOrNull() ?: 0.0
        val precoComDesconto = produto.produtoPreco * (1 - descontoPorcentagem / 100)
        val precoComDescontoFormatado = numberFormat.format(precoComDesconto)


        viewHolder.valor.setTypeface(null, Typeface.NORMAL) // Resetando para normal
        viewHolder.valor.setTextColor(Color.parseColor("#606060")) // Resetando para cor padrão
        viewHolder.desconto.visibility = View.GONE // Esconde o desconto por padrão

        // Verifica se o desconto é válido e não é zero
        if (descontoPorcentagem > 0) {
            // Exibe o valor original e o valor com desconto
            viewHolder.valor.text = precoFormatado
            viewHolder.desconto.text = precoComDescontoFormatado
            viewHolder.desconto.visibility = View.VISIBLE // Certifique-se de que o desconto esteja visível

            viewHolder.valor.setTypeface(viewHolder.valor.typeface, Typeface.NORMAL) // Define o estilo normal
            viewHolder.valor.setTextColor(Color.parseColor("#606060")) // Exemplo de cor para o valor original
            viewHolder.valor.textSize = 12f // Tamanho da fonte do valor original
        } else {
            // Apenas exibe o valor original, escondendo o TextView de desconto
            viewHolder.valor.text = precoFormatado
            viewHolder.desconto.visibility = View.GONE
            viewHolder.valor.setTextColor(Color.parseColor("#34C9FF"))
            viewHolder.valor.textSize = 15f // Define o tamanho de fonte do desconto
            viewHolder.valor.setTypeface(viewHolder.valor.typeface, Typeface.BOLD) // Define o estilo em negrito
        }

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

    // Função para atualizar a lista de produtos e notificar mudanças
    fun atualizarLista(novaLista: List<Produto>) {
        dataSet = novaLista
        notifyDataSetChanged()
    }

    }
