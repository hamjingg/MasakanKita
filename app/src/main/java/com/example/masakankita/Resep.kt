package com.example.masakankita

import androidx.annotation.DrawableRes

data class Resep(
    val id: Long,
    val nama: String,
    val kategori: String,
    val bahan: String,
    val langkah: String
)

const val EXTRA_RESEP_ID = "extra_resep_id"
val PILIHAN_KATEGORI = listOf("Nasi", "Mie", "Sup", "Lainnya")

@DrawableRes
fun gambarUntukKategori(kategori: String): Int = when (kategori) {
    "Nasi" -> R.drawable.masakan_nasi
    "Mie" -> R.drawable.masakan_mie
    "Sup" -> R.drawable.masakan_sup
    else -> R.drawable.masakan_lainnya
}
