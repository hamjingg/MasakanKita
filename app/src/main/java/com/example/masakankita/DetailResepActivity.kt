package com.example.masakankita

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DetailResepActivity : ComponentActivity() {
    private lateinit var db: ResepDatabaseHelper
    private var idResep: Long = -1L
    private var resep by mutableStateOf<Resep?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idResep = intent.getLongExtra(EXTRA_RESEP_ID, -1L)
        if (idResep <= 0) {
            Toast.makeText(this, "ID resep tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        db = ResepDatabaseHelper(this)
        setContent {
            TemaMasakanKita {
                HalamanDetail(
                    resep = resep,
                    kembali = { finish() },
                    edit = {
                        startActivity(Intent(this, FormResepActivity::class.java).putExtra(EXTRA_RESEP_ID, idResep))
                    },
                    hapus = {
                        try {
                            if (db.hapus(idResep)) {
                                Toast.makeText(this, "Resep berhasil dihapus", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this, "Resep sudah tidak ada", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "Gagal menghapus: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::db.isInitialized) {
            try {
                resep = db.resepById(idResep)
            } catch (e: Exception) {
                Toast.makeText(this, "Gagal memuat detail resep", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        if (::db.isInitialized) db.close()
        super.onDestroy()
    }
}

@Composable
private fun HalamanDetail(resep: Resep?, kembali: () -> Unit, edit: () -> Unit, hapus: () -> Unit) {
    var dialogHapus by remember { mutableStateOf(false) }

    if (dialogHapus) {
        AlertDialog(
            onDismissRequest = { dialogHapus = false },
            title = { Text("Hapus resep?") },
            text = { Text("Resep ini akan dihapus dari database lokal.") },
            confirmButton = {
                TextButton(onClick = { dialogHapus = false; hapus() }) {
                    Text("Hapus", color = Color(0xFFB3261E))
                }
            },
            dismissButton = { TextButton(onClick = { dialogHapus = false }) { Text("Batal") } }
        )
    }

    Scaffold(
        containerColor = Latar,
        topBar = { HeaderHalaman("Detail Resep", kembali) },
        bottomBar = {
            if (resep != null) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = { dialogHapus = true }, modifier = Modifier.weight(1f)) {
                        Text("Hapus")
                    }
                    Button(onClick = edit, modifier = Modifier.weight(1f)) {
                        Text("Edit Resep")
                    }
                }
            }
        }
    ) { padding ->
        if (resep == null) {
            Column(Modifier.padding(padding).padding(20.dp)) {
                Text("Resep tidak ditemukan.")
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .verticalScroll(rememberScrollState()).padding(20.dp)
            ) {
                Image(
                    painter = painterResource(gambarUntukKategori(resep.kategori)),
                    contentDescription = "Gambar ${resep.nama}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(23.dp))
                )
                Spacer(Modifier.height(17.dp))
                Text(resep.kategori.uppercase(), color = HijauUtama, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(resep.nama, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(20.dp))
                BagianResep("🥬  Bahan-bahan", resep.bahan)
                Spacer(Modifier.height(16.dp))
                BagianResep("👩‍🍳  Cara memasak", resep.langkah)
            }
        }
    }
}

@Composable
private fun BagianResep(judul: String, isi: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(judul, fontSize = 17.sp, color = HijauUtama, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text(isi, fontSize = 15.sp, lineHeight = 24.sp)
        }
    }
}
