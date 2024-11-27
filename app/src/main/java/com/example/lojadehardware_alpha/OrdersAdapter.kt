package com.example.lojadehardware_alpha

import Pedidos
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OrdersAdapter(private val pedidosList: List<Pedidos>) : RecyclerView.Adapter<OrdersAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidosList[position]

        // Preenche os dados do pedido
        holder.pedidoNumero.text = "Pedido #${pedido.PEDIDO_ID}"
        holder.totalPedido.text = "Total: R$ ${"%.2f".format(pedido.TOTAL_PEDIDO)}"

        // Usando Glide para carregar a imagem do produto
        Glide.with(holder.itemView.context)
            .load(pedido.PRODUTO_IMAGEM)
            .into(holder.produtoImagem)

        // Adicionar evento de clique para abrir detalhes do pedido
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PedidoDetalhe::class.java)
            intent.putExtra("PEDIDO_ID", pedido.PEDIDO_ID) // Passa o ID do pedido
            context.startActivity(intent) // Abre a tela PedidoDetalhe
        }
    }

    override fun getItemCount(): Int {
        return pedidosList.size
    }

    // ViewHolder para os elementos do pedido
    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pedidoNumero: TextView = itemView.findViewById(R.id.pedidoNumero)
        val totalPedido: TextView = itemView.findViewById(R.id.totalPedido)
        val produtoImagem: ImageView = itemView.findViewById(R.id.produtoImagem)
    }
}