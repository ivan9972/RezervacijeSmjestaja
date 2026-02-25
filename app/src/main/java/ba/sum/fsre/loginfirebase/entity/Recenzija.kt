package ba.sum.fsre.loginfirebase.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recenzije",
    foreignKeys = [
        ForeignKey(
            entity = Korisnik::class,
            parentColumns = ["id"],
            childColumns = ["korisnikId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Smjestaj::class,
            parentColumns = ["id"],
            childColumns = ["smjestajId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("korisnikId"),
        Index("smjestajId"),
        Index(value = ["korisnikId", "smjestajId"], unique = true)
    ]
)
data class Recenzija(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val korisnikId: Int,
    val smjestajId: Int,
    val ocjena: Int,
    val komentar: String,
    val datumKreiranja: Long = System.currentTimeMillis()
)