import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM user_favorites WHERE userUid = :userUid AND productId = :productId")
    suspend fun removeFavorite(userUid: String, productId: Long)

    @Query("SELECT COUNT(*) FROM user_favorites WHERE productId = :productId")
    suspend fun getFavoriteCount(productId: Long): Int

    @Query("SELECT productId FROM user_favorites WHERE userUid = :userUid")
    suspend fun getUserFavoriteProductIds(userUid: String): List<Long>

    @Query("SELECT productId FROM user_favorites WHERE userUid = :userUid")
    fun getUserFavoriteProductIdsFlow(userUid: String): Flow<List<Long>>

    @Query("DELETE FROM user_favorites WHERE userUid = :userUid AND productId IN (:productIds)")
    suspend fun deleteFavorites(userUid: String, productIds: List<Long>)

    @Query("SELECT EXISTS(SELECT 1 FROM user_favorites WHERE userUid = :userUid AND productId = :productId LIMIT 1)")
    suspend fun isProductFavorite(userUid: String, productId: Long): Boolean

    @Query("DELETE FROM user_favorites WHERE userUid = :userUid AND productId = :productId")
    suspend fun deleteFavorite(userUid: String, productId: Long): Int
}
