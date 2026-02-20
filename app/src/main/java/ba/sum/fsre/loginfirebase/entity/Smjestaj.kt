package ba.sum.fsre.loginfirebase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smjestaji")
data class Smjestaj(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val naziv: String,
    val opis: String,
    val grad: String,
    val adresa: String,
    val vlasnikId: Int
)