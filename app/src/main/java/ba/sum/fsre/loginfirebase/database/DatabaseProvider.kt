package ba.sum.fsre.loginfirebase.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile private var INSTANCE: AppDatabase? = null

    fun get(ctx: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                ctx.applicationContext,
                AppDatabase::class.java,
                "rezervacije_db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
    }
}