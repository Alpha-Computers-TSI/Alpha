package com.example.lojadehardware_alpha

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_produto_detalhes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_produto_detalhes)

        val nomeProduto = intent.getStringExtra("NOME_PRODUTO")
        val descricaoProduto = intent.getStringExtra("DESCRICAO_PRODUTO")

        findViewById<TextView>(R.id.txtNomeProduto).text = nomeProduto
        findViewById<TextView>(R.id.txtDescricaoProduto).text = descricaoProduto

        findViewById<Button>(R.id.btnAdicionarAoCarrinho).setOnClickListener {

        }

        }
    }
