package com.example.lojadehardware_alpha

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.GsonBuilder
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.NumberFormat
import java.util.Locale

class Payment : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var radioGroup: RadioGroup
    private lateinit var qrCodeTitleContainer: LinearLayout
    private lateinit var boletoTitleContainer: LinearLayout
    private lateinit var cardTitleContainer: LinearLayout
    private lateinit var qrCodeContainer: LinearLayout
    private lateinit var boletoContainer: LinearLayout
    private lateinit var cardContainer: LinearLayout
    private lateinit var goBackToProductCart: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Configuração do layout para considerar barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialização de SharedPreferences
        sharedPreferences = getSharedPreferences("Dados", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        // Receber dados da Intent
        val totalValue = intent.getStringExtra("TOTAL")?.toDoubleOrNull()
        val productList = intent.getParcelableArrayListExtra<Produto>("PRODUCT_LIST")

        // Exibir valor total formatado
        val totalValueFormatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(totalValue)
        findViewById<TextView>(R.id.totalValueText).text = "Total: $totalValueFormatted"

        // Configuração de views
        radioGroup = findViewById(R.id.addressRadioGroup)
        qrCodeTitleContainer = findViewById(R.id.qrCodeTitleContainer)
        boletoTitleContainer = findViewById(R.id.boletoTitleContainer)
        cardTitleContainer = findViewById(R.id.cardTitleContainer)

        qrCodeContainer = findViewById(R.id.qrCodeContainer)
        boletoContainer = findViewById(R.id.boletoContainer)
        cardContainer = findViewById(R.id.cardContainer)

        val allContainers = listOf(qrCodeContainer, boletoContainer, cardContainer)

        // Configuração de alternância entre métodos de pagamento
        qrCodeTitleContainer.setOnClickListener {
            toggleVisibility(qrCodeContainer, allContainers)
        }

        boletoTitleContainer.setOnClickListener {
            toggleVisibility(boletoContainer, allContainers)
        }

        cardTitleContainer.setOnClickListener {
            toggleVisibility(cardContainer, allContainers)
        }

        // Carregar endereços do usuário
        loadUserAddresses(userId)

        // Volta para o Carrinho
        goBackToProductCart = findViewById(R.id.goBackToProductCart)
        goBackToProductCart.setOnClickListener {
            val intent = Intent(this, ProductCart::class.java)
            startActivity(intent)
        }

        //Volta para a tela de produtos
        val goBackCartBtn: Button = findViewById(R.id.goBackCartBtn)
        goBackCartBtn.setOnClickListener {
            val intent = Intent(this, ProductCart::class.java)
            startActivity(intent)
        }

        // Configurar botão de finalização do pagamento
        val finishPaymentButton: Button = findViewById(R.id.finishPaymentButton)
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

    // Alterna visibilidade dos containers
    private fun toggleVisibility(containerToShow: LinearLayout, allContainers: List<LinearLayout>) {
        allContainers.forEach { container ->
            container.visibility = if (container == containerToShow && container.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    // Carrega endereços do usuário via API
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
                        if (addresses.isEmpty()) {
                            // Se não houver endereços, mostra o layout de "endereço não encontrado"
                            findViewById<LinearLayout>(R.id.layoutAddressNotFound).visibility = View.VISIBLE
                        } else {
                            // Se houver endereços, popula o RadioGroup e esconde o layout de "endereço não encontrado"
                            populateAddressRadioButtons(addresses)
                        }
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

    // Popula RadioGroup com endereços do usuário
    private fun populateAddressRadioButtons(addresses: List<Endereco>) {
        addresses.forEach { address ->
            val radioButton = RadioButton(this)
            radioButton.text =
                "${address.ENDERECO_LOGRADOURO}, ${address.ENDERECO_NUMERO} - ${address.ENDERECO_NOME}, ${address.ENDERECO_CIDADE} - ${address.ENDERECO_ESTADO}, ${address.ENDERECO_CEP}"
            radioButton.tag = address.hashCode() // Como o modelo não tem um ID, usamos o hashCode para identificação
            radioGroup.addView(radioButton)
        }
    }

    // Envia ordem de compra via API
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

                    // Cria a Intent para a próxima tela
                    val intent = Intent(this@Payment, OrderPlaced::class.java)

                    // Passa a lista de produtos pela Intent
                    if (!products.isNullOrEmpty()) {
                        intent.putParcelableArrayListExtra("PRODUCT_LIST", products)
                    }

                    startActivity(intent)
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


    // Interface para chamadas da API
    interface OrderApiService {
        @GET("ALPHA/finalizar_compra/getUserAddresses")
        fun getUserAddresses(@Query("userId") userId: Int): Call<List<Endereco>>

        @POST("ALPHA/finalizar_compra/createOrder/index.php")
        fun createOrder(@Body orderRequest: OrderRequest): Call<ResponseCompra>
    }
}
