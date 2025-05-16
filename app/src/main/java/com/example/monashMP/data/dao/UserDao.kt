package com.example.monashMP.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.monashMP.data.entity.User
import com.example.monashMP.data.entity.UserWithProducts
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>): List<Long>

    @Update
    suspend fun updateUser(user: User): Int

    @Delete
    suspend fun deleteUser(user: User): Int

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUserById(userId: Long): Int

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE nickName LIKE :searchQuery")
    suspend fun searchUsersByNickName(searchQuery: String): List<User>

    @Query("SELECT * FROM users ORDER BY birthday ASC")
    suspend fun getUsersSortedByBirthday(): List<User>

    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserWithProducts(userId: Long): UserWithProducts?

    @Transaction
    @Query("SELECT * FROM users")
    suspend fun getAllUsersWithProducts(): List<UserWithProducts>

}
