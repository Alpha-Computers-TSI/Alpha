    package com.example.lojadehardware_alpha

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide

    // Adapter para os itens do pedido
    class PedidoDetalheAdapter(
        private val itens: List<PedidoItem> // Lista de itens do pedido
    ) : RecyclerView.Adapter<PedidoDetalheAdapter.PedidoDetalheViewHolder>() {

        // ViewHolder para o item da lista
        class PedidoDetalheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val produtoImagem: ImageView = itemView.findViewById(R.id.produtoImagem)
            val produtoNome: TextView = itemView.findViewById(R.id.textViewNomeProduto)
            val descricaoProduto: TextView = itemView.findViewById(R.id.textViewDescricaoProduto)
            val precoProduto: TextView = itemView.findViewById(R.id.textViewPrecoProduto)
            val textViewQuantidade: TextView = itemView.findViewById(R.id.textViewQuantidade)
        }

        // Inflando o layout do item
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoDetalheViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detalhes_pedidos, parent, false) // Corrigido para o layout correto
            return PedidoDetalheViewHolder(view)
        }

        // Vinculando os dados ao ViewHolder
        override fun onBindViewHolder(holder: PedidoDetalheViewHolder, position: Int) {
            val item = itens[position]

            // Carregando a imagem do produto com Glide
            Glide.with(holder.itemView.context)
                .load(item.IMAGEM_URL)
                .placeholder(R.drawable.erro)
                .error(R.drawable.erro)
                .into(holder.produtoImagem)

            // Configurando os textos
            holder.textViewQuantidade.text = String.format("Qtd: %d", item.ITEM_QTD)
            holder.descricaoProduto.text = item.PRODUTO_DESC
            holder.produtoNome.text = item.PRODUTO_NOME
            holder.precoProduto.text = String.format("R$ %.2f", item.ITEM_PRECO_COM_DESCONTO)
        }

        // Retornando o tamanho da lista
        override fun getItemCount(): Int {
            return itens.size
        }
    }