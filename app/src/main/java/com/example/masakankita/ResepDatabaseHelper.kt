package com.example.masakankita

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Database lokal sederhana; tanpa internet dan tanpa akun. */
class ResepDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "masakan_kita.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE resep (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                kategori TEXT NOT NULL,
                bahan TEXT NOT NULL,
                langkah TEXT NOT NULL
            )""".trimIndent()
        )

        // Contoh data saat aplikasi pertama kali dipasang.
        isiContoh(db, "Nasi Goreng Sederhana", "Nasi",
            "1 piring nasi putih\n1 butir telur\n2 siung bawang putih\nKecap dan garam secukupnya",
            "1. Tumis bawang putih sampai harum.\n2. Masukkan telur, lalu orak-arik.\n3. Masukkan nasi dan aduk rata.\n4. Tambahkan kecap dan garam. Sajikan.")
        isiContoh(db, "Mie Goreng Rumahan", "Mie",
            "1 bungkus mie telur\n1 butir telur\nSawi secukupnya\nBawang putih dan kecap",
            "1. Rebus mie lalu tiriskan.\n2. Tumis bawang putih dan telur.\n3. Tambahkan sawi dan mie.\n4. Bumbui dengan kecap, aduk sampai matang.")
        isiContoh(db, "Sup Ayam Hangat", "Sup",
            "150 gram ayam\n1 buah wortel\n1 buah kentang\nAir, garam, dan daun bawang",
            "1. Rebus ayam hingga matang.\n2. Tambahkan wortel dan kentang.\n3. Masukkan garam sesuai selera.\n4. Taburi daun bawang lalu sajikan.")
    }

    private fun isiContoh(db: SQLiteDatabase, nama: String, kategori: String, bahan: String, langkah: String) {
        db.insertOrThrow("resep", null, nilai(nama, kategori, bahan, langkah))
    }

    private fun nilai(nama: String, kategori: String, bahan: String, langkah: String) =
        ContentValues().apply {
            put("nama", nama)
            put("kategori", kategori)
            put("bahan", bahan)
            put("langkah", langkah)
        }

    private fun dariCursor(c: Cursor): Resep = Resep(
        id = c.getLong(c.getColumnIndexOrThrow("id")),
        nama = c.getString(c.getColumnIndexOrThrow("nama")),
        kategori = c.getString(c.getColumnIndexOrThrow("kategori")),
        bahan = c.getString(c.getColumnIndexOrThrow("bahan")),
        langkah = c.getString(c.getColumnIndexOrThrow("langkah"))
    )

    fun semuaResep(): List<Resep> {
        val hasil = mutableListOf<Resep>()
        readableDatabase.query("resep", null, null, null, null, null, "id DESC").use { c ->
            while (c.moveToNext()) hasil.add(dariCursor(c))
        }
        return hasil
    }

    fun resepById(id: Long): Resep? {
        readableDatabase.query("resep", null, "id = ?", arrayOf(id.toString()), null, null, null)
            .use { c -> return if (c.moveToFirst()) dariCursor(c) else null }
    }

    fun tambah(nama: String, kategori: String, bahan: String, langkah: String): Long =
        writableDatabase.insertOrThrow("resep", null, nilai(nama, kategori, bahan, langkah))

    fun ubah(id: Long, nama: String, kategori: String, bahan: String, langkah: String): Boolean =
        writableDatabase.update("resep", nilai(nama, kategori, bahan, langkah), "id = ?", arrayOf(id.toString())) > 0

    fun hapus(id: Long): Boolean =
        writableDatabase.delete("resep", "id = ?", arrayOf(id.toString())) > 0

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Tidak ada perubahan skema untuk versi 1.
    }
}
