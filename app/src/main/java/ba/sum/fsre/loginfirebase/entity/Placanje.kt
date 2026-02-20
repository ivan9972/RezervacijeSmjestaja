package ba.sum.fsre.loginfirebase.entity

import androidx.room.*

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
    indices = [Index("rezervacijaId")]
)
data class Placanje(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val rezervacijaId: Int,
    val iznos: Double,
    val datumPlacanja: Long,
    val nacinPlacanja: String,
    val status: String
)