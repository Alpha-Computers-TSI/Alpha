package com.example.lojadehardware_alpha

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.util.Locale

class activity_produto_detalhes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_produto_detalhes)

        val nomeProduto = intent.getStringExtra("PRODUTO_NOME")
        val categoriaProduto = intent.getStringExtra("CATEGORIA_ID")
        val descricaoProduto = intent.getStringExtra("PRODUTO_DESC")
        val precoProduto = intent.getStringExtra("PRODUTO_PRECO")

        findViewById<TextView>(R.id.txtNomeProduto).text = nomeProduto
        findViewById<TextView>(R.id.txtCategoriaProduto).text = categoriaProduto
        findViewById<TextView>(R.id.txtDescricaoProduto).text = descricaoProduto

        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        findViewById<TextView>(R.id.txtPrecoProduto).text = numberFormat.format(precoProduto)

        findViewById<Button>(R.id.btnAdicionarAoCarrinho).setOnClickListener {

        }
    }
}
