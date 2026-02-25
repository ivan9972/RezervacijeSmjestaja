package ba.sum.fsre.loginfirebase.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sobe",
    foreignKeys = [
        ForeignKey(
            entity = Smjestaj::class,
            parentColumns = ["id"],
            childColumns = ["smjestajId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("smjestajId")]
)
data class Soba(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val smjestajId: Int,
    val brojSobe: String,
    val tip: String,
    val kapacitet: Int,
    val cijenaPoNoci: Double,

    val dostupna: Boolean = true
)