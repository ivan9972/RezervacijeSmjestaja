package ba.sum.fsre.loginfirebase.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "smjestaji",
    foreignKeys = [
        ForeignKey(
            entity = Korisnik::class,
            parentColumns = ["id"],
            childColumns = ["vlasnikId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("vlasnikId")]
)
data class Smjestaj(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val naziv: String,
    val opis: String,
    val grad: String,
    val adresa: String,
    val vlasnikId: Int
)