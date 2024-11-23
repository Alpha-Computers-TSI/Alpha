package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

class Register : AppCompatActivity() {
    private lateinit var nomeInput: EditText
    private lateinit var cpfInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var senhaInput: EditText
    private lateinit var confirmaSenhaInput: EditText
    private lateinit var registerBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.myOrdersTextView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nomeInput = findViewById(R.id.nomeInput)
        cpfInput = findViewById(R.id.cpfInput)
        emailInput = findViewById(R.id.emailInput)
        senhaInput = findViewById(R.id.senhaInput)
        confirmaSenhaInput = findViewById(R.id.confirmaSenhaInput)
        registerBtn = findViewById(R.id.registerBtn)

        registerBtn.setOnClickListener {
            val nome = nomeInput.text.toString()
            val cpf = cpfInput.text.toString()
            val email = emailInput.text.toString()
            val senha = senhaInput.text.toString()

            if (nome.isNotEmpty() && cpf.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty()) {
                val user = User(nome, cpf, email, senha)
                registerUser(user)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(user: User) {
        // Configuração do Retrofit diretamente na tela
        val retrofit = Retrofit.Builder()
            .baseUrl("https://eb995d1f-dfff-4a7b-90f7-7ebe2438ad50-00-8qvsbwqugcqv.kirk.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()

        val userApi = retrofit.create(UserApi::class.java)

        userApi.createUser(user).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.success) {
                            Toast.makeText(this@Register, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Register, Login::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@Register, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@Register, "Erro ao registrar usuário.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@Register, "Falha na conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Interface para Retrofit
    interface UserApi {
        @POST("userRegister.php")
        fun createUser(@Body user: User): Call<ApiResponse>
    }

    // Modelo de dados para o usuário
    data class User(
        @SerializedName("nome") val nome: String,
        @SerializedName("cpf") val cpf: String,
        @SerializedName("email") val email: String,
        @SerializedName("senha") val senha: String
    )

    // Modelo para a resposta da API
    data class ApiResponse(
        val success: Boolean,
        val message: String
    )
}
