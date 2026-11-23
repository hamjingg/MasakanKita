package com.example.masakankita

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val HijauUtama = Color(0xFF196B4E)
val HijauLembut = Color(0xFFE4F3E9)
val Latar = Color(0xFFF8FAF7)
val TeksGelap = Color(0xFF173F30)

@Composable
fun TemaMasakanKita(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = HijauUtama,
            onPrimary = Color.White,
            primaryContainer = HijauLembut,
            background = Latar,
            surface = Color.White,
            onSurface = TeksGelap
        ),
        content = content
    )
}

@Composable
fun HeaderHalaman(judul: String, kembali: () -> Unit) {
    Surface(color = HijauUtama, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.statusBarsPadding().padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            TextButton(onClick = kembali) {
                Text("‹", color = Color.White, fontSize = 30.sp, lineHeight = 30.sp)
            }
            Text(
                judul,
                modifier = Modifier.padding(start = 6.dp, top = 10.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}
