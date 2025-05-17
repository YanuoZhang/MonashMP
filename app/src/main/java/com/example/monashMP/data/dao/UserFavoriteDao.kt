import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: UserFavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: UserFavoriteEntity)

    @Query("SELECT * FROM user_favorites WHERE userUid = :userUid")
    suspend fun getFavoritesByUser(userUid: String): List<UserFavoriteEntity>

    @Query("SELECT productId FROM user_favorites WHERE userUid = :userUid")
    fun getFavoriteProductIdsFlow(userUid: String): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_favorites WHERE userUid = :userUid AND productId = :productId)")
    suspend fun isFavorite(userUid: String, productId: Long): Boolean
}

