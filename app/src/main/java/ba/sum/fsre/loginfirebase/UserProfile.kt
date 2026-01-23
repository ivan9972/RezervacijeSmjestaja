package ba.sum.fsre.usermanagement

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profile",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    //val darkMode: Boolean=false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
)