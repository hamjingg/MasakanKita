package com.example.masakankita

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private lateinit var db: ResepDatabaseHelper
    private var daftarResep by mutableStateOf<List<Resep>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ResepDatabaseHelper(this)
        setContent {
            TemaMasakanKita {
                HalamanUtama(
                    resep = daftarResep,
                    bukaDetail = { id ->
                        startActivity(Intent(this, DetailResepActivity::class.java).putExtra(EXTRA_RESEP_ID, id))
                    },
                    tambahResep = { startActivity(Intent(this, FormResepActivity::class.java)) }
                )
            }
        }
    }

    // Dibaca ulang saat kembali dari Tambah/Edit/Hapus, langsung memperbarui UI.
    override fun onResume() {
        super.onResume()
        try {
            daftarResep = db.semuaResep()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membaca resep: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}

@Composable
private fun HalamanUtama(resep: List<Resep>, bukaDetail: (Long) -> Unit, tambahResep: () -> Unit) {
    var pencarian by rememberSaveable { mutableStateOf("") }
    val hasil = resep.filter {
        it.nama.contains(pencarian.trim(), ignoreCase = true) ||
        it.kategori.contains(pencarian.trim(), ignoreCase = true)
    }

    Scaffold(
        containerColor = Latar,
        topBar = {
            Box(Modifier.fillMaxWidth().statusBarsPadding()) {
                Column(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                        .padding(horizontal = 22.dp, vertical = 19.dp)
                ) {
                    Text("Masakan Kita 🍳", fontWeight = FontWeight.Bold, fontSize = 27.sp, color = HijauUtama)
                    Text("Kumpulan resep simpel buat sehari-hari", fontSize = 13.sp, color = Color.DarkGray)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = tambahResep, containerColor = HijauUtama, contentColor = Color.White) {
                Text("+", fontSize = 30.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)
        ) {
            OutlinedTextField(
                value = pencarian,
                onValueChange = { pencarian = it },
                label = { Text("Cari nama resep atau kategori") },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(18.dp))
            Text("Resep pilihan (${hasil.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(10.dp))
            if (hasil.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada resep yang cocok.\nTap + untuk menambahkan.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(hasil, key = { it.id }) { item ->
                        KartuResep(item, onClick = { bukaDetail(item.id) })
                    }
                    item { Spacer(Modifier.height(78.dp)) }
                }
            }
        }
    }
}

@Composable
private fun KartuResep(item: Resep, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(gambarUntukKategori(item.kategori)),
                contentDescription = "Gambar ${item.nama}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(width = 110.dp, height = 92.dp).clip(RoundedCornerShape(14.dp))
            )
            Column(Modifier.weight(1f).padding(start = 13.dp)) {
                Text(item.kategori.uppercase(), color = HijauUtama, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(item.nama, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(5.dp))
                Text("Lihat bahan & cara memasak ›", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}
