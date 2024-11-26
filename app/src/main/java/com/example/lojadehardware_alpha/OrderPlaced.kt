package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderPlaced : AppCompatActivity() {
    private lateinit var resumoPedidoRecyclerView: RecyclerView
    private lateinit var goBackToProductsBtn: Button
    private lateinit var goBackToHomeArrow: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_placed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        goBackToHomeArrow = findViewById(R.id.goBackToHomeArrow)

        goBackToHomeArrow.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        // Recuperar a lista de produtos passada pelo Intent
        val productList = intent.getParcelableArrayListExtra<Produto>("PRODUCT_LIST")

        // Verificar se a lista é nula ou vazia
        if (productList.isNullOrEmpty()) {
            // Exiba uma mensagem ou realize outra ação para lidar com a lista vazia
            println("Nenhum produto encontrado!")
            return
        }

        // Configurar RecyclerView
        resumoPedidoRecyclerView = findViewById(R.id.resumoPedidoRecyclerView)
        resumoPedidoRecyclerView.layoutManager = LinearLayoutManager(this) // Define o layout vertical
        resumoPedidoRecyclerView.adapter = ResumoPedidoAdapter(productList) // Associa o adapter

        goBackToProductsBtn = findViewById(R.id.goBackToProductsBtn)

        goBackToProductsBtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}