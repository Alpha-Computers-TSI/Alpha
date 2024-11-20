import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lojadehardware_alpha.R
import com.example.lojadehardware_alpha.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Profile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // IDs dos EditTexts
        val nomeEditText = findViewById<EditText>(R.id.nomeEditText)
        val cpfEditText = findViewById<EditText>(R.id.cpfEditText)
        val telefoneEditText = findViewById<EditText>(R.id.telefoneEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)

        // Configuração do Retrofit dentro da classe
      val retrofit = Retrofit.Builder()
            .baseUrl("https://027c2e5f-4e20-4907-8ddb-002cce23454a-00-2bk0k8130zh8s.kirk.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

     val usuarioApi = retrofit.create(UsuarioApi::class.java)

        // ID do usuário (exemplo: 1)
        val usuarioId = 1

        // Chamada da API para buscar os dados do usuário
        usuarioApi.getUsuario(usuarioId).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    val usuario = response.body()
                    if (usuario != null) {
                        // Preenchendo os campos com os dados do usuário
                        nomeEditText.setText(usuario.USUARIO_NOME)
                        cpfEditText.setText(usuario.USUARIO_CPF)
                        emailEditText.setText(usuario.USUARIO_EMAIL)
                        telefoneEditText.setText("Telefone não disponível no modelo")
                    }
                } else {
                    Toast.makeText(this@Profile, "Erro ao buscar usuário", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(this@Profile, "Falha na conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // Interface da API
    interface UsuarioApi {
        @retrofit2.http.GET("usuarios/{id}")
        fun getUsuario(@retrofit2.http.Path("id") id: Int): Call<Usuario>
    }
}
