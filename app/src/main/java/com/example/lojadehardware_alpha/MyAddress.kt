package com.example.lojadehardware_alpha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyAddress : AppCompatActivity() {

    private lateinit var cepEdit: EditText
    private lateinit var logradouroEdit: EditText
    private lateinit var numeroEdit: EditText
    private lateinit var nomeEdit: EditText
    private lateinit var cidadeEdit: EditText
    private lateinit var estadoEdit: EditText
    private lateinit var updateButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var bntVoltarMeusDados: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://ca639ef2-1d78-467b-b48a-91e14f4a2f8b-00-37irjmq3m5iwx.spock.replit.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService = retrofit.create(UserService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_address)

        // Inicializar os componentes do layout
        cepEdit = findViewById(R.id.cepEdit)
        logradouroEdit = findViewById(R.id.logradouroEdit)
        numeroEdit = findViewById(R.id.numeroEdit)
        nomeEdit = findViewById(R.id.nomeEdit)
        cidadeEdit = findViewById(R.id.cidadeEdit)
        estadoEdit = findViewById(R.id.estadoEdit)
        updateButton = findViewById(R.id.buttonUpdate)
        progressBar = findViewById(R.id.progressBar)

        //botao voltar para meus dados
        bntVoltarMeusDados = findViewById(R.id.bntVoltarMeusDados)
        bntVoltarMeusDados.setOnClickListener{
            val intent = Intent(this, MyAccount::class.java)
            startActivity(intent)
            finish()
        }


        // Clique para registrar um novo endereço
        val textregister: TextView = findViewById(R.id.textregister)
        textregister.setOnClickListener {
            val intent = Intent(this, RegisterAddress::class.java)
            startActivity(intent)
            finish()
        }

        // Recuperar ID do usuário do SharedPreferences
        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", -1)

        if (userId != -1) {
            loadInitialAddress(userId) // Carregar os dados iniciais do endereço
        } else {
            Toast.makeText(this, "Erro: ID do usuário não encontrado.", Toast.LENGTH_LONG).show()
        }

        // Configurar o botão de atualização
        updateButton.setOnClickListener {
            val endereco = Endereco(
                ENDERECO_CEP = cepEdit.text.toString().trim(),
                ENDERECO_LOGRADOURO = logradouroEdit.text.toString().trim(),
                ENDERECO_NUMERO = numeroEdit.text.toString().trim(),
                ENDERECO_NOME = nomeEdit.text.toString().trim(),
                ENDERECO_CIDADE = cidadeEdit.text.toString().trim(),
                ENDERECO_ESTADO = estadoEdit.text.toString().trim()
            )

            updateAddress(userId, endereco)
        }
    }

    // Função para carregar o endereço inicial
    private fun loadInitialAddress(userId: Int) {
        progressBar.visibility = ProgressBar.VISIBLE

        userService.listEnderecos(userId).enqueue(object : Callback<Endereco> {
            override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {
                progressBar.visibility = ProgressBar.GONE
                if (response.isSuccessful) {
                    response.body()?.let { endereco ->
                        cepEdit.setText(endereco.ENDERECO_CEP)
                        logradouroEdit.setText(endereco.ENDERECO_LOGRADOURO)
                        numeroEdit.setText(endereco.ENDERECO_NUMERO)
                        nomeEdit.setText(endereco.ENDERECO_NOME)
                        cidadeEdit.setText(endereco.ENDERECO_CIDADE)
                        estadoEdit.setText(endereco.ENDERECO_ESTADO)
                    } ?: run {
                        Toast.makeText(this@MyAddress, "Nenhum endereço encontrado.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MyAddress, "Erro ao carregar endereço: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Endereco>, t: Throwable) {
                Toast.makeText(this@MyAddress, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Função para atualizar o endereço
    private fun updateAddress(userId: Int, endereco: Endereco) {
        progressBar.visibility = ProgressBar.VISIBLE
        userService.updateEndereco(userId, endereco).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressBar.visibility = ProgressBar.GONE
                if (response.isSuccessful) {
                    Toast.makeText(this@MyAddress, "Endereço atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MyAddress, "Erro ao atualizar endereço: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MyAddress, "Erro na conexão com a API.", Toast.LENGTH_LONG).show()
            }
        })
    }
}
