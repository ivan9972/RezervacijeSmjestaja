package ba.sum.fsre.loginfirebase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "korisnici")
data class Korisnik(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ime: String,
    val prezime: String,
    val email: String,
    val telefon: String?,
    val datumKreiranja: Long,
    val firebaseUid: String
)