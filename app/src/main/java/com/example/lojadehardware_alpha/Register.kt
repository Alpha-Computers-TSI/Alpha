package com.example.lojadehardware_alpha

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var loginTxt: TextView


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
        loginTxt = findViewById(R.id.loginTxt)

        loginTxt.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        registerBtn.setOnClickListener {
            val nome = nomeInput.text.toString()
            val cpf = cpfInput.text.toString()
            val email = emailInput.text.toString()
            val senha = senhaInput.text.toString()
            val confirmaSenha = confirmaSenhaInput.text.toString()

            // Verifica se todos os campos foram preenchidos
            if (nome.isNotEmpty() && cpf.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && confirmaSenha.isNotEmpty()) {
                // Verifica se a senha e a confirmação de senha são iguais
                if (senha == confirmaSenha) {
                    val user = User(nome, cpf, email, senha)
                    registerUser(user)
                } else {
                    // Exibe um aviso se as senhas não coincidirem
                    Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Exibe um aviso se algum campo estiver vazio
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun registerUser(user: User) {
        // Configuração do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://2c87926d-7bca-4d8a-b846-4ddddb31c316-00-1y6vahvqnlnmn.worf.repl.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userApi = retrofit.create(UserApi::class.java)

        // Envia a requisição de registro
        userApi.createUser(user).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                // Verifica se a resposta é válida e contém um corpo
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        // Registro bem-sucedido
                        Toast.makeText(this@Register, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
                        // Navega para a tela de login
                        startActivity(Intent(this@Register, Login::class.java))
                        finish()
                    } else {
                        // Mensagem de erro retornada pela API
                        val errorMessage = apiResponse?.message ?: "Erro desconhecido ao registrar usuário."
                        Toast.makeText(this@Register, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Caso a resposta não seja bem-sucedida
                    val error = "Erro no registro: Código HTTP ${response.code()} - ${response.message()}"
                    Log.e("RegisterActivity", error)
                    Toast.makeText(this@Register, error, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Falha na conexão
                val failureMessage = "Falha na conexão: ${t.message}"
                Log.e("RegisterActivity", failureMessage)
                Toast.makeText(this@Register, failureMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Interface para Retrofit
    interface UserApi {
        @POST("userRegister.php")
        fun createUser(@Body user: User): Call<ApiResponse>
    }

    // Modelo de dados do usuário
    data class User(
        @SerializedName("nome") val nome: String,
        @SerializedName("cpf") val cpf: String,
        @SerializedName("email") val email: String,
        @SerializedName("senha") val senha: String
    )

    // Modelo da resposta da API
    data class ApiResponse(
        val success: Boolean,
        val message: String
    )
}