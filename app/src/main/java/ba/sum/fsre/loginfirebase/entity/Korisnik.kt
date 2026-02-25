package ba.sum.fsre.loginfirebase.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "korisnici",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["firebaseUid"], unique = true)
    ]
)
data class Korisnik(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ime: String,
    val prezime: String,
    val email: String,
    val telefon: String? = null,
    val datumKreiranja: Long = System.currentTimeMillis(),
    val firebaseUid: String
)