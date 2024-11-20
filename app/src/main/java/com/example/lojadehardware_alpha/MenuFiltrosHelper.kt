package com.example.lojadehardware_alpha.util

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.PopupMenu
import com.example.lojadehardware_alpha.R

class MenuFiltrosHelper(
    private val context: Context,
    private val filtroButton: Button,
    private val onFiltroSelecionado: (String) -> Unit
) {

    fun mostrarMenuFiltros(anchorView: View) {
        val popupMenu = PopupMenu(context, anchorView)
        popupMenu.menuInflater.inflate(R.menu.menu_ordenar, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val filtroSelecionado = when (menuItem.itemId) {
                R.id.menu_maior -> "Preço maior"
                R.id.menu_menor -> "Preço menor"
                R.id.menu_mais_recentes -> "Mais recentes"
                R.id.menu_mais_vendidos -> "Mais vendidos"
                else -> null
            }

            filtroSelecionado?.let {
                filtroButton.text = menuItem.title // Atualiza o texto do botão
                onFiltroSelecionado(it) // Notifica o filtro selecionado
            }
            true
        }

        popupMenu.show()
    }

}