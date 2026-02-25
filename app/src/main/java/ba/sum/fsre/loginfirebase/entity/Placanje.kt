package ba.sum.fsre.loginfirebase.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "placanja",
    foreignKeys = [
        ForeignKey(
            entity = Rezervacija::class,
            parentColumns = ["id"],
            childColumns = ["rezervacijaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("rezervacijaId", unique = true)
    ]
)
data class Placanje(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val rezervacijaId: Int,
    val iznos: Double,
    val datumPlacanja: Long = System.currentTimeMillis(),
    val nacinPlacanja: String,
    val status: String
)