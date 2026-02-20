package ba.sum.fsre.loginfirebase.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ba.sum.fsre.loginfirebase.dao.*
import ba.sum.fsre.loginfirebase.entity.*

@Database(
    entities = [
        Korisnik::class,
        Smjestaj::class,
        Soba::class,
        Rezervacija::class,
        Placanje::class,
        Recenzija::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun korisnikDao(): KorisnikDao
    abstract fun smjestajDao(): SmjestajDao
    abstract fun sobaDao(): SobaDao
    abstract fun rezervacijaDao(): RezervacijaDao
    abstract fun placanjeDao(): PlacanjeDao
    abstract fun recenzijaDao(): RecenzijaDao
}