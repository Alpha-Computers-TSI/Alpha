package com.example.lojadehardware_alpha

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyAccount : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.myOrdersTextView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView)

        // Marcar a aba atual como selecionada
        bottomNavigationView.selectedItemId = R.id.nav_account

        val myDocuments: TextView = findViewById(R.id.myDocuments)

        myDocuments.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        val myAddress: TextView = findViewById(R.id.myAddress)

        myAddress.setOnClickListener{
            val intent = Intent(this, MyAddress::class.java)
            startActivity(intent)
        }

        val myOrdersTextView: TextView = findViewById(R.id.myOrdersTextView)

        myOrdersTextView.setOnClickListener{
            val intent = Intent(this, Orders::class.java)
            startActivity(intent)
        }
    }
}