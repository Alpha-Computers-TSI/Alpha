    package com.example.lojadehardware_alpha

    import android.app.Activity
    import android.content.Intent
    import android.content.res.ColorStateList
    import android.graphics.PorterDuff
    import android.os.Bundle
    import android.text.Editable
    import android.text.TextWatcher
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageButton
    import android.widget.SeekBar
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.widget.SwitchCompat
    import androidx.core.content.ContextCompat

    class FiltrosActivity : AppCompatActivity() {

        private lateinit var switchDesconto: SwitchCompat
        private lateinit var switchEstoque: SwitchCompat
        private lateinit var editPrecoMin: EditText
        private lateinit var editPrecoMax: EditText
        private lateinit var btnAplicarFiltros: Button
        private lateinit var btnLimparFiltros: Button
        private lateinit var textPrecoMax: TextView
        private lateinit var seekBarPrecoMax: SeekBar

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_filtros)

            // Inicializar os componentes da tela
            switchDesconto = findViewById(R.id.switchDesconto)
            switchEstoque = findViewById(R.id.switchEstoque)
            editPrecoMin = findViewById(R.id.editPrecoMin)
            editPrecoMax = findViewById(R.id.editPrecoMax)
            btnAplicarFiltros = findViewById(R.id.btnAplicarFiltros)
            btnLimparFiltros = findViewById(R.id.btnLimparFiltros)
            textPrecoMax = findViewById(R.id.textPrecoMax)
            seekBarPrecoMax = findViewById(R.id.seekBarPrecoMax)

            val switchEstoque: SwitchCompat = findViewById(R.id.switchEstoque)
            val switchDesconto: SwitchCompat = findViewById(R.id.switchDesconto)

            // Manipulando o switch de estoque
            switchEstoque.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Quando o switchEstoque estiver ligado
                    switchEstoque.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
                    switchEstoque.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
                } else {
                    // Quando o switchEstoque estiver desligado
                    switchEstoque.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
                    switchEstoque.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray))
                }
            }

            // Manipulando o switch de desconto
            switchDesconto.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Quando o switchDesconto estiver ligado
                    switchDesconto.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
                    switchDesconto.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
                } else {
                    // Quando o switchDesconto estiver desligado
                    switchDesconto.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
                    switchDesconto.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray))
                }
            }

            val seekBar: SeekBar = findViewById(R.id.seekBarPrecoMax)
            // Definir a cor da barra de progresso
            val progressDrawable = seekBar.progressDrawable.mutate()
            progressDrawable.setColorFilter(ContextCompat.getColor(this, R.color.blue), PorterDuff.Mode.SRC_IN)

            // Configurar valores iniciais
            val descontoInicial = intent.getBooleanExtra("FILTRO_DESCONTO", false)
            val estoqueInicial = intent.getBooleanExtra("FILTRO_ESTOQUE", false)
            val precoMinAtual = intent.getFloatExtra("FILTRO_PRECO_MIN", 0f)
            val precoMaxAtual = intent.getFloatExtra("FILTRO_PRECO_MAX", 1000f).toInt()
            val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)


            btnVoltar.setOnClickListener {
                finish() // Finaliza a Activity atual e retorna à anterior
            }

            switchDesconto.isChecked = descontoInicial
            switchEstoque.isChecked = estoqueInicial
            editPrecoMin.setText(if (precoMinAtual > 0f) precoMinAtual.toString() else "")
            editPrecoMax.setText(precoMaxAtual.toString())
            seekBarPrecoMax.progress = precoMaxAtual
            textPrecoMax.text = "Valor máximo: R$ $precoMaxAtual"

            // Sincronizar SeekBar com TextView e EditText
            seekBarPrecoMax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    textPrecoMax.text = "Valor máximo: R$ $progress"
                    if (fromUser) {
                        editPrecoMax.setText(progress.toString())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // Sincronizar EditText com SeekBar
            editPrecoMax.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val input = s.toString().toIntOrNull()
                    if (input != null) {
                        // Garantir que o valor está dentro dos limites da SeekBar
                        if (input in 0..seekBarPrecoMax.max) {
                            seekBarPrecoMax.progress = input
                        } else if (input > seekBarPrecoMax.max) {
                            seekBarPrecoMax.progress = seekBarPrecoMax.max
                            editPrecoMax.setText(seekBarPrecoMax.max.toString())
                        }
                    }
                }
            })

            // Botão para aplicar filtros
            btnAplicarFiltros.setOnClickListener {
                val precoMin = editPrecoMin.text.toString().toFloatOrNull() ?: 0f
                val precoMax = seekBarPrecoMax.progress.toFloat() // Priorizar SeekBar

                val resultIntent = Intent().apply {
                    putExtra("FILTRO_DESCONTO", switchDesconto.isChecked)
                    putExtra("FILTRO_ESTOQUE", switchEstoque.isChecked)
                    putExtra("FILTRO_PRECO_MIN", precoMin)
                    putExtra("FILTRO_PRECO_MAX", precoMax ?: 5000f)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }

            // Botão para limpar filtros
            btnLimparFiltros.setOnClickListener {
                switchDesconto.isChecked = false
                switchEstoque.isChecked = false
                editPrecoMin.text.clear()
                editPrecoMax.text.clear()
                seekBarPrecoMax.progress = 5000 // Valor padrão
                textPrecoMax.text = "Valor máximo: R$ 5000"
            }
        }
    }