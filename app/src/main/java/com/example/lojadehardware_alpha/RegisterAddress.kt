package com.example.lojadehardware_alpha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterAddress : AppCompatActivity() {

    // Inicializa Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://ca639ef2-1d78-467b-b48a-91e14f4a2f8b-00-37irjmq3m5iwx.spock.replit.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val enderecoApi = retrofit.create(UserService::class.java)

    private val viaCepRetrofit = Retrofit.Builder()
        .baseUrl("https://viacep.com.br/ws/")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        )
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        )
        .build()



    private val viaCep = viaCepRetrofit.create(ViaCepService::class.java)


    // Componentes de UI
    private lateinit var cepInput: EditText
    private lateinit var logradouroInput: EditText
    private lateinit var numeroInput: EditText
    private lateinit var nomeInput: EditText
    private lateinit var cidadeInput: EditText
    private lateinit var estadoInput: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var textmyaddress: TextView
    private lateinit var btnvoltarMeuEndereco: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_address)

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Marcar a aba atual como selecionada
        bottomNavigationView.selectedItemId = R.id.nav_account

        BottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView)


        // Vincular componentes da UI
        cepInput = findViewById(R.id.cepInput)
        logradouroInput = findViewById(R.id.logradouroInput)
        numeroInput = findViewById(R.id.numeroInput)
        nomeInput = findViewById(R.id.nomeInput)
        cidadeInput = findViewById(R.id.cidadeInput)
        estadoInput = findViewById(R.id.estadoInput)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        progressBar = findViewById(R.id.progressBar)
        textmyaddress = findViewById(R.id.textmyaddress) // Aqui é onde o TextView é encontrado no layout

        cepInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val cep = s.toString()
                if (cep.length == 8) { // Verifica se o CEP tem 8 dígitos
                    buscarEndereco(cep)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Evento de clique no botão "Cadastrar"
        btnCadastrar.setOnClickListener {
            progressBar.visibility = ProgressBar.VISIBLE

            val endereco = Endereco(
                ENDERECO_CEP = cepInput.text.toString(),
                ENDERECO_LOGRADOURO = logradouroInput.text.toString(),
                ENDERECO_NUMERO = numeroInput.text.toString(),
                ENDERECO_NOME = nomeInput.text.toString(),
                ENDERECO_CIDADE = cidadeInput.text.toString(),
                ENDERECO_ESTADO = estadoInput.text.toString()
            )

            // Recuperar ID do usuário do SharedPreferences
            val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("id", -1)

            enderecoApi.cadastrarEndereco(userId, endereco)
                .enqueue(object : Callback<ResponseModel> {
                    override fun onResponse(
                        call: Call<ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
                        if (response.isSuccessful) {
                            progressBar.visibility = ProgressBar.GONE

                            response.body()?.let {
                                if (it.success != null) {
                                    Toast.makeText(this@RegisterAddress, it.success, Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@RegisterAddress, it.error, Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            progressBar.visibility = ProgressBar.GONE

                            Toast.makeText(this@RegisterAddress, "Erro: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        Toast.makeText(this@RegisterAddress, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }

        // Evento de clique para navegar até a tela de endereço
        textmyaddress.setOnClickListener {
            val intent = Intent(this, MyAddress::class.java)
            startActivity(intent)
        }

        // Configurar o botão de voltar

        btnvoltarMeuEndereco= findViewById(R.id.btnvoltarMeuEndereco)
        btnvoltarMeuEndereco.setOnClickListener {
            val intent = Intent(this, MyAddress::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun buscarEndereco(cep: String) {
        viaCep.buscarEndereco(cep).enqueue(object : Callback<ViaCepResponse> {
            override fun onResponse(call: Call<ViaCepResponse>, response: Response<ViaCepResponse>) {
                progressBar.visibility = ProgressBar.GONE
                if (response.isSuccessful) {
                    val endereco = response.body()
                    if (endereco != null) {
                        // Preenche os campos automaticamente
                        logradouroInput.setText(endereco.logradouro ?: "")
                        cidadeInput.setText(endereco.localidade ?: "")
                        estadoInput.setText(endereco.uf ?: "")
                    } else {
                        Toast.makeText(this@RegisterAddress, "CEP não encontrado.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterAddress, "Erro ao buscar o CEP: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ViaCepResponse>, t: Throwable) {
                Toast.makeText(this@RegisterAddress, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
