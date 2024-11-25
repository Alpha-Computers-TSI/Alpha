package com.example.lojadehardware_alpha

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNavigationHelper {

    fun setupBottomNavigation(activity: Activity, bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (activity !is Home) {
                        activity.startActivity(Intent(activity, Home::class.java))
                    }
                    true
                }

                R.id.nav_categories -> {
                    if (activity !is ListaCategorias) {
                        activity.startActivity(Intent(activity, ListaCategorias::class.java))
                    }
                    true
                }

                R.id.nav_account -> {
                    if (activity !is MyAccount) {
                        activity.startActivity(Intent(activity, MyAccount::class.java))
                    }
                    true
                }

                else -> false
            }
        }
    }
}
