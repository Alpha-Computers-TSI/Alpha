package com.example.lojadehardware_alpha

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class FiltrosActivity : AppCompatActivity() {

    private lateinit var switchDesconto: SwitchCompat
    private lateinit var switchEstoque: SwitchCompat
    private lateinit var btnAplicarFiltros: Button
    private lateinit var btnLimparFiltros: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtros)

        switchDesconto = findViewById(R.id.switchDesconto)
        switchEstoque = findViewById(R.id.switchEstoque)
        btnAplicarFiltros = findViewById(R.id.btnAplicarFiltros)
        btnLimparFiltros = findViewById(R.id.btnLimparFiltros)

        val descontoInicial = intent.getBooleanExtra("FILTRO_DESCONTO", false)
        val estoqueInicial = intent.getBooleanExtra("FILTRO_ESTOQUE", false)

        switchDesconto.isChecked = descontoInicial
        switchEstoque.isChecked = estoqueInicial

        btnAplicarFiltros.setOnClickListener {
            val resultIntent = intent.apply {
                putExtra("FILTRO_DESCONTO", switchDesconto.isChecked)
                putExtra("FILTRO_ESTOQUE", switchEstoque.isChecked)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        btnLimparFiltros.setOnClickListener {
            switchDesconto.isChecked = false
            switchEstoque.isChecked = false
        }
    }
}