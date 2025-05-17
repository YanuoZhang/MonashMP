
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.monashMP.data.entity.ProductEntity

@Entity(
    tableName = "user_favorites",
    primaryKeys = ["userUid", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userUid"), Index("productId")]
)
data class UserFavoriteEntity(
    @ColumnInfo(name = "userUid")
    val userUid: String,

    @ColumnInfo(name = "productId")
    val productId: Long,

    @ColumnInfo(name = "favoriteDate")
    val favoriteDate: Long = System.currentTimeMillis()
)
