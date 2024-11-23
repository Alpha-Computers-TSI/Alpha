package com.example.lojadehardware_alpha

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyAddress : AppCompatActivity() {

    private lateinit var cepEdit: EditText
    private lateinit var roadEdit: EditText
    private lateinit var numberEdit: EditText
    private lateinit var updateButton: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://eb995d1f-dfff-4a7b-90f7-7ebe2438ad50-00-8qvsbwqugcqv.kirk.replit.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService = retrofit.create(UserService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_address)

        // Inicializar os componentes do layout
        cepEdit = findViewById(R.id.cepEdit)
        roadEdit = findViewById(R.id.roadEdit)
        numberEdit = findViewById(R.id.numberEdit)
        updateButton = findViewById(R.id.buttonUpdate)

        // Recuperar o ID do usuário do SharedPreferences
        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", -1)

        Log.d("MyAddress", "Recuperado userId: $userId")

        // Verificar se o userId é válido
        if (userId != -1) {
            loadInitialAddress(userId) // Carregar os dados iniciais do endereço
            Log.d("MyAddress", "Recuperado userId: $userId")
        } else {
            Log.e("MyAddress", "Erro: ID do usuário não encontrado.")
            Toast.makeText(this, "Erro: ID do usuário não encontrado.", Toast.LENGTH_LONG).show()
        }

//        // Configurar o botão de atualização
//        updateButton.setOnClickListener {
//            val endereco = Endereco(
//                ENDERECO_CEP = cepEdit.text.toString(),
//                ENDERECO_ID = userId, // Usando o ID do usuário como identificador
//                ENDERECO_LOGRADOURO = roadEdit.text.toString(),
//                ENDERECO_NUMERO = numberEdit.text.toString()
//            )
//            updateAddress(userId, endereco)
//        }
    }

    // Função para carregar o endereço inicial
    private fun loadInitialAddress(userId: Int) {
        userService.listEnderecos(userId).enqueue(object : Callback<Endereco> {
            override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {
                if (response.isSuccessful) {
                    val endereco = response.body()
                    if (endereco != null) {
                        cepEdit.setText(endereco.ENDERECO_CEP)
                        roadEdit.setText(endereco.ENDERECO_LOGRADOURO)
                        numberEdit.setText(endereco.ENDERECO_NUMERO)
                        Log.d("MyAddress", "Endereço carregado com sucesso.")
                    } else {
                        Log.e("MyAddress", "Endereço não encontrado.")
                    }
                } else {
                    Log.e("MyAddress", "Erro ao carregar endereço: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Endereco>, t: Throwable) {
                Log.e("MyAddress", "Erro de conexão: ${t.message}")
            }
        })
    }


//    // Função para atualizar o endereço
//    private fun updateAddress(userId: Int, endereco: Endereco) {
//        userService.updateEndereco(userId, endereco).enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    Toast.makeText(this@MyAddress, "Endereço atualizado com sucesso!", Toast.LENGTH_SHORT).show()
//                    Log.d("MyAddress", "Endereço atualizado com sucesso!")
//                } else {
//                    Log.e("MyAddress", "Erro ao atualizar endereço: ${response.code()}")
//                    Toast.makeText(this@MyAddress, "Erro ao atualizar endereço.", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                Log.e("MyAddress", "Erro na conexão com a API: ${t.message}")
//                Toast.makeText(this@MyAddress, "Erro ao atualizar endereço.", Toast.LENGTH_LONG).show()
//            }
//        })
//    }

}
