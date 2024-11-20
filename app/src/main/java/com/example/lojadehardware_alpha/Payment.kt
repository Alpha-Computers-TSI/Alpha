package com.example.lojadehardware_alpha

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.google.gson.GsonBuilder

class Payment : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("Dados", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        val totalValue = intent.getStringExtra("TOTAL")?.toDoubleOrNull()
        val productList = intent.getParcelableArrayListExtra<Produto>("PRODUCT_LIST") // Recebe a lista de produtos do Intent

        findViewById<TextView>(R.id.totalValueText).text = "Total: $totalValue"

        val cardNumberInput: EditText = findViewById(R.id.cardNumberInput)
        val cardExpirationInput: EditText = findViewById(R.id.cardExpirationInput)
        val cardCVCInput: EditText = findViewById(R.id.cardCVCInput)
        val finishPaymentButton: Button = findViewById(R.id.finishPaymentButton)

        radioGroup = findViewById(R.id.addressRadioGroup)

        // Carregar endereços
        loadUserAddresses(userId)

        // Configurar botão para finalizar pagamento
        finishPaymentButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedAddressId = selectedRadioButton.tag.toString().toInt()
                enviaOrdem(userId, totalValue ?: 0.0, productList, selectedAddressId)
            } else {
                Toast.makeText(this, "Por favor, selecione um endereço", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadUserAddresses(userId: Int) {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val api = retrofit.create(OrderApiService::class.java)
        val call = api.getUserAddresses(userId)
        call.enqueue(object : Callback<List<Endereco>> {
            override fun onResponse(call: Call<List<Endereco>>, response: Response<List<Endereco>>) {
                if (response.isSuccessful) {
                    response.body()?.let { addresses ->
                        populateAddressRadioButtons(addresses)
                    }
                } else {
                    Toast.makeText(this@Payment, "Erro ao carregar endereços", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Endereco>>, t: Throwable) {
                Toast.makeText(this@Payment, "Falha na conexão", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun populateAddressRadioButtons(addresses: List<Endereco>) {
        addresses.forEach { address ->
            val radioButton = RadioButton(this)
            radioButton.text = "${address.enderecoLogradouro}, ${address.enderecoNumero} - ${address.enderecoComplemento}, ${address.enderecoCidade} - ${address.enderecoEstado}, ${address.enderecoCep}"
            radioButton.tag = address.enderecoId
            radioGroup.addView(radioButton)
        }
    }

    private fun enviaOrdem(userId: Int, total: Double, products: ArrayList<Produto>?, addressId: Int) {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val api = retrofit.create(OrderApiService::class.java)
        val orderRequest = OrderRequest(userId, total, products ?: arrayListOf(), addressId)
        val call = api.createOrder(orderRequest)
        call.enqueue(object : Callback<ResponseCompra> {
            override fun onResponse(call: Call<ResponseCompra>, response: Response<ResponseCompra>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Payment, "Pedido realizado com sucesso!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@Payment, Home::class.java))
                    finish()
                } else {
                    Toast.makeText(this@Payment, "Erro ao realizar pedido", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseCompra>, t: Throwable) {
                Toast.makeText(this@Payment, "Falha na conexão", Toast.LENGTH_LONG).show()
            }
        })
    }

    interface OrderApiService {
        @GET("ALPHA/finalizar_compra/getUserAddresses")
        fun getUserAddresses(@Query("userId") userId: Int): Call<List<Endereco>>

        @POST("ALPHA/finalizar_compra/createOrder")
        fun createOrder(@Body orderRequest: OrderRequest): Call<ResponseCompra>
    }
}
