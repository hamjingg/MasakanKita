package com.example.masakankita

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FormResepActivity : ComponentActivity() {
    private lateinit var db: ResepDatabaseHelper
    private var idResep: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ResepDatabaseHelper(this)
        idResep = intent.getLongExtra(EXTRA_RESEP_ID, -1L)
        val awal = try {
            if (idResep > 0L) db.resepById(idResep) else null
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuka resep", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        if (idResep > 0 && awal == null) {
            Toast.makeText(this, "Resep tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            TemaMasakanKita {
                HalamanForm(
                    awal = awal,
                    kembali = { finish() },
                    simpan = { nama, kategori, bahan, langkah ->
                        try {
                            val berhasil = if (idResep > 0) {
                                db.ubah(idResep, nama, kategori, bahan, langkah)
                            } else {
                                db.tambah(nama, kategori, bahan, langkah) > 0
                            }
                            if (berhasil) {
                                Toast.makeText(this, "Resep berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this, "Data tidak berhasil disimpan", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "Gagal menyimpan: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}

@Composable
private fun HalamanForm(
    awal: Resep?,
    kembali: () -> Unit,
    simpan: (nama: String, kategori: String, bahan: String, langkah: String) -> Unit
) {
    // rememberSaveable mempertahankan ketikan ketika layar diputar.
    var nama by rememberSaveable { mutableStateOf(awal?.nama.orEmpty()) }
    var kategori by rememberSaveable { mutableStateOf(awal?.kategori ?: "Nasi") }
    var bahan by rememberSaveable { mutableStateOf(awal?.bahan.orEmpty()) }
    var langkah by rememberSaveable { mutableStateOf(awal?.langkah.orEmpty()) }
    var cekValidasi by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Latar,
        topBar = { HeaderHalaman(if (awal == null) "Tambah Resep" else "Edit Resep", kembali) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            Text("Isi resep masakan kamu", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Semua kolom wajib diisi ya.", fontSize = 13.sp)
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama masakan") },
                placeholder = { Text("Contoh: Ayam Goreng") },
                singleLine = true,
                isError = cekValidasi && nama.isBlank(),
                supportingText = {
                    if (cekValidasi && nama.isBlank()) Text("Nama masakan harus diisi")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text("Kategori / gambar resep", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                PILIHAN_KATEGORI.forEach { pilihan ->
                    FilterChip(
                        selected = kategori == pilihan,
                        onClick = { kategori = pilihan },
                        label = { Text(pilihan) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = bahan,
                onValueChange = { bahan = it },
                label = { Text("Bahan-bahan") },
                placeholder = { Text("Tulis satu bahan per baris...") },
                minLines = 4,
                isError = cekValidasi && bahan.isBlank(),
                supportingText = {
                    if (cekValidasi && bahan.isBlank()) Text("Bahan-bahan harus diisi")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = langkah,
                onValueChange = { langkah = it },
                label = { Text("Langkah memasak") },
                placeholder = { Text("1. Siapkan bahan\n2. Panaskan wajan...") },
                minLines = 5,
                isError = cekValidasi && langkah.isBlank(),
                supportingText = {
                    if (cekValidasi && langkah.isBlank()) Text("Langkah memasak harus diisi")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    cekValidasi = true
                    if (nama.isNotBlank() && bahan.isNotBlank() && langkah.isNotBlank() &&
                        kategori in PILIHAN_KATEGORI
                    ) {
                        simpan(nama.trim(), kategori, bahan.trim(), langkah.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(if (awal == null) "Simpan Resep" else "Simpan Perubahan", fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
