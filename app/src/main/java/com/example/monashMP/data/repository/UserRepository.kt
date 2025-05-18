package com.example.monashMP.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.monashMP.data.model.UserModel
import com.example.monashMP.utils.md5
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepository(private val context: Context) {

    private val db = FirebaseDatabase.getInstance().reference

    suspend fun login(email: String, password: String): String? = safeCall {
        val hashedPassword = password.md5()
        val snapshot = db.child("users").orderByChild("email").equalTo(email).get().await()
        if (snapshot.exists()) {
            for (child in snapshot.children) {
                val userPassword = child.child("password").getValue(String::class.java)
                val uid = child.key
                if (userPassword == hashedPassword && uid != null) {
                    return@safeCall uid
                }
            }
        }
        null
    }


    suspend fun getUserByEmail(email: String): Boolean = safeCall {
        val snapshot = db.child("users").orderByChild("email").equalTo(email).get().await()
        snapshot.exists()
    } ?: false

    suspend fun uploadAvatarToFirebase(userId: String, bitmap: Bitmap): String = suspendCoroutine { cont ->
        val storageRef = FirebaseStorage.getInstance().reference.child("avatars/$userId.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                storageRef.downloadUrl
            }
            .addOnSuccessListener { uri ->
                cont.resume(uri.toString())
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

    suspend fun registerUser(uid: String, userMap: Map<String, Any>) = safeCall {
        db.child("users").child(uid).setValue(userMap).await()
    }

    suspend fun getUserByUid(uid: String): UserModel? = safeCall {
        val snapshot = db.child("users").child(uid).get().await()
        if (snapshot.exists()) {
            UserModel(
                uid = uid,
                email = snapshot.child("email").value as? String ?: "",
                avatarUrl = snapshot.child("avatarUrl").value as? String ?: "",
                nickname = snapshot.child("nickname").value as? String ?: "",
                birthday = snapshot.child("birthday").value as? String ?: "",
                primaryCampus = snapshot.child("campus").value as? String ?: "",
                createdAt = snapshot.child("createdAt").value.toString().toLongOrNull() ?: 0L
            )
        } else {
            null
        }
    }

    private suspend fun <T> safeCall(block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            Log.e("FirebaseSafeCall", "Error: ${e.message}", e)
            null
        }
    }
}
