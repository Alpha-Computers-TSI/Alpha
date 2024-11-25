package com.example.lojadehardware_alpha

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Profile : AppCompatActivity() {

    private lateinit var nomeEditText: EditText
    private lateinit var cpfEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView)

        // Marcar a aba atual como selecionada
        bottomNavigationView.selectedItemId = R.id.nav_account

        // Inicializar os EditText
        nomeEditText = findViewById(R.id.userEditText)
        cpfEditText = findViewById(R.id.cpfEditText)
        emailEditText = findViewById(R.id.emailEditText)
        updateButton = findViewById(R.id.button10)

        // Recuperar o ID do usuário do SharedPreferences
        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", -1)

        Log.d("Profile", "Recuperado userId: $userId")

        // Verifique se o userId é válido
        if (userId != -1) {
            Log.d("Profile", "User ID: $userId")
            fetchUserData(userId)
        } else {
            Log.e("Profile", "Erro: ID do usuário não encontrado.")
            Toast.makeText(this, "Erro: ID do usuário não encontrado.", Toast.LENGTH_LONG).show()
        }

        // Configura o botão de atualizar
        updateButton.setOnClickListener {
            updateUserData(userId)
        }
    }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://2c87926d-7bca-4d8a-b846-4ddddb31c316-00-1y6vahvqnlnmn.worf.repl.co/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService = retrofit.create(UserService::class.java)

    private fun updateUserData(userId: Int) {
        val nome = nomeEditText.text.toString()
        val cpf = cpfEditText.text.toString()
        val email = emailEditText.text.toString()

        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        val usuarioAtualizado = Usuario(nome = nome, cpf = cpf, email = email)

        userService.updateUser(userId, usuarioAtualizado).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Profile, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Profile, "Falha ao atualizar: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("Profile", "Falha na atualização: ${response.code()} ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@Profile, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Profile", "Erro na API: ${t.message}")
            }
        })
    }

    fun fetchUserData(userId: Int) {
        val call = userService.getUsuario(userId)
        call.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    val usuario = response.body()
                    if (usuario != null) {
                        nomeEditText.setText(usuario.nome)
                        cpfEditText.setText(usuario.cpf)
                        emailEditText.setText(usuario.email)
                    } else {
                        Log.e("Erro", "Objeto usuário está nulo.")
                    }
                } else {
                    Log.e("Erro", "Resposta não foi bem-sucedida: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Log.e("Erro", "Falha na requisição: ${t.message}")
            }
        })
    }




}
