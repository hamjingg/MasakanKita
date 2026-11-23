# Masakan Kita 🍳

**Project Android Studio simpel** tentang kumpulan resep masakan, dibuat menggunakan Kotlin + Jetpack Compose + SQLite. Semua data disimpan **offline di HP**, tanpa akun dan tanpa internet.

## Cara menjalankan

1. Ekstrak ZIP `MasakanKita_AndroidStudio.zip`.
2. Buka **Android Studio** → **Open** → pilih folder `MasakanKita` (yang ada `settings.gradle.kts`-nya).
3. Tunggu **Gradle Sync** selesai. Pastikan internet aktif saat sync pertama, **JDK 17** dipilih di Gradle Settings, dan Android SDK Platform 35 terpasang.
4. Pilih emulator atau HP Android (minimum Android 7.0 / API 24), lalu klik **Run ▶**.
5. Tiga resep contoh langsung tersedia. Tekan **+** untuk menambah, tap resep untuk melihat detail, lalu pilih **Edit** atau **Hapus**.

> Catatan: bila versi Android Studio lama belum mendukung Android Gradle Plugin 8.7.3, gunakan Android Studio yang lebih baru. Aplikasi belum dibuild/test di lingkungan ini karena Android SDK dan Gradle tidak tersedia. Launcher Gradle sederhana disertakan untuk mengambil distribusi resmi Gradle 8.9 pada penggunaan pertama; ini bukan berkas wrapper resmi Gradle.

## File penting

- `MainActivity.kt`: layar beranda (READ), pencarian, navigasi ke form & detail.
- `DetailResepActivity.kt`: layar detail resep, EDIT/HAPUS.
- `FormResepActivity.kt`: form tambah/edit, client-side validation.
- `ResepDatabaseHelper.kt`: SQLite, CREATE/READ/UPDATE/DELETE serta seed resep.
- `Tema.kt`: warna, header, tampilan dasar Compose.
- `Resep.kt`: model data, kategori dan pemetaan gambar.
- `app/src/main/res/drawable/`: gambar masakan PNG.

## Pemetaan ke tugas a–g

| Syarat | Implementasi |
|---|---|
| a. Column, Row, Box, Text, OutlinedTextField, Button, Image | Ada di beranda, detail dan form |
| b. Image dari drawable | PNG `masakan_nasi`, `masakan_mie`, `masakan_sup`, `masakan_lainnya` melalui `painterResource` |
| c. Min. 2 layar, Explicit Intent | 3 Activity: Main, Detail, Form dengan `Intent(this, ...::class.java)` |
| d. Form validation, defensive programming, state loss, reactive UI | Nama/bahan/langkah wajib, operasi DB try/catch, `rememberSaveable` pada isian dan pencarian, `mutableStateOf` menyegarkan list/detail |
| e. CRUD | Tambah, baca, ubah, hapus resep |
| f. Data di database lokal | SQLiteOpenHelper, file `masakan_kita.db` |
| g. Dapat dijalankan | Project Android Studio siap dibuka dan dijalankan setelah Gradle sync; belum dilakukan build langsung di lingkungan pembuat |

## Skenario demo buat dosen

1. Buka beranda dan tunjukkan gambar makanan.
2. Pilih satu resep → tampil detail bahan dan langkah (**Read**).
3. Tekan Edit, ganti nama resep → Simpan (**Update**).
4. Tekan `+`, coba Simpan tanpa isi nama → muncul error validasi.
5. Isi semua kolom → Simpan (**Create**).
6. Buka resep baru, tekan Hapus → dialog konfirmasi (**Delete**).
7. Tambah resep lagi, putar HP saat mengetik → isian tetap ada (`rememberSaveable`).
8. Tutup aplikasi lalu buka lagi → resep tersimpan di database lokal.

**Nama paket:** `com.example.masakankita`

## Catatan launcher Gradle

Folder `gradle/wrapper/` memuat `BootstrapGradleWrapperMain.java` dan JAR hasil kompilasi, sebagai pengganti bootstrap resmi yang tidak tersedia pada lingkungan pembuatan. Launcher ini hanya mengunduh `https://services.gradle.org/distributions/gradle-8.9-bin.zip` dan menjalankan Gradle hasil unduhan. Jika ingin mengganti dengan **Gradle Wrapper resmi**, setelah Gradle tersedia jalankan `gradle wrapper --gradle-version 8.9`, lalu gunakan berkas wrapper resmi hasil generate.
