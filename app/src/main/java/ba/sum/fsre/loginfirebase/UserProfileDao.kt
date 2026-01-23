package ba.sum.fsre.usermanagement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(profile: UserProfile): Long

    @Query("SELECT * FROM user_profile WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserProfile?

    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): UserProfile?

    @Query("UPDATE user_profile SET lastLoginAt = :ts WHERE id = :id")
    suspend fun updateLastLogin(id: Long, ts: Long = System.currentTimeMillis())

    @Query("SELECT * FROM user_profile ORDER BY createdAt DESC")
    suspend fun getAllProfiles(): List<UserProfile>
}