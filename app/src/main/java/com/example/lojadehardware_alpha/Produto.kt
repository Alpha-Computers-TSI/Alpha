package com.example.lojadehardware_alpha

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Produto(
    @SerializedName("PRODUTO_ID") val produtoId: Int?,
    @SerializedName("PRODUTO_NOME") val produtoNome: String?,
    @SerializedName("PRODUTO_DESC") val produtoDesc: String?,
    @SerializedName("PRODUTO_PRECO") val produtoPreco: Double?,
    @SerializedName("PRODUTO_DESCONTO") val produtoDesconto: Double?,
    @SerializedName("CATEGORIA_ID") val categoriaId: Int?,
    @SerializedName("PRODUTO_ATIVO") val produtoAtivo: Int?,
    @SerializedName("IMAGEM_URL") val imagemUrl: String?,
    @SerializedName("QUANTIDADE_DISPONIVEL") var quantidadeDisponivel: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(produtoId)
        parcel.writeString(produtoNome)
        parcel.writeString(produtoDesc)
        parcel.writeValue(produtoPreco)
        parcel.writeValue(produtoDesconto)
        parcel.writeValue(categoriaId)
        parcel.writeValue(produtoAtivo)
        parcel.writeString(imagemUrl)
        parcel.writeValue(quantidadeDisponivel)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Produto> {
        override fun createFromParcel(parcel: Parcel): Produto = Produto(parcel)
        override fun newArray(size: Int): Array<Produto?> = arrayOfNulls(size)
    }
}