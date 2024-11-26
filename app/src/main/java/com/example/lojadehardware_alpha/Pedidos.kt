data class  Pedidos(
    val PEDIDO_ID: Int,
    val USUARIO_ID: Int,
    val STATUS_ID: Int,
    val PRODUTO_IMAGEM: String,  // URL da imagem do produto
    val TOTAL_PEDIDO: Double    // Soma total do pedido
)