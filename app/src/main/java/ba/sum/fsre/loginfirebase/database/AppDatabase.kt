package ba.sum.fsre.loginfirebase.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ba.sum.fsre.loginfirebase.dao.KorisnikDao
import ba.sum.fsre.loginfirebase.dao.PlacanjeDao
import ba.sum.fsre.loginfirebase.dao.RecenzijaDao
import ba.sum.fsre.loginfirebase.dao.RezervacijaDao
import ba.sum.fsre.loginfirebase.dao.SmjestajDao
import ba.sum.fsre.loginfirebase.dao.SobaDao
import ba.sum.fsre.loginfirebase.entity.Korisnik
import ba.sum.fsre.loginfirebase.entity.Placanje
import ba.sum.fsre.loginfirebase.entity.Recenzija
import ba.sum.fsre.loginfirebase.entity.Rezervacija
import ba.sum.fsre.loginfirebase.entity.Smjestaj
import ba.sum.fsre.loginfirebase.entity.Soba

@Database(
    entities = [
        Korisnik::class,
        Smjestaj::class,
        Soba::class,
        Rezervacija::class,
        Placanje::class,
        Recenzija::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun korisnikDao(): KorisnikDao
    abstract fun smjestajDao(): SmjestajDao
    abstract fun sobaDao(): SobaDao
    abstract fun rezervacijaDao(): RezervacijaDao
    abstract fun placanjeDao(): PlacanjeDao
    abstract fun recenzijaDao(): RecenzijaDao
}