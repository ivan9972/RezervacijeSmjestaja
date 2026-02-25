package ba.sum.fsre.loginfirebase.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rezervacije",
    foreignKeys = [
        ForeignKey(
            entity = Korisnik::class,
            parentColumns = ["id"],
            childColumns = ["korisnikId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Soba::class,
            parentColumns = ["id"],
            childColumns = ["sobaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("korisnikId"), Index("sobaId")]
)
data class Rezervacija(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val korisnikId: Int,
    val sobaId: Int,
    val datumDolaska: Long,
    val datumOdlaska: Long,
    val ukupnaCijena: Double,

    val status: String = "CONFIRMED"
)