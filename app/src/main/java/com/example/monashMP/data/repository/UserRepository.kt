package com.example.monashMP.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.monashMP.data.model.UserModel
import com.example.monashMP.utils.UserSessionManager
import com.example.monashMP.utils.md5
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserRepository(private val context: Context) {

    private val db = FirebaseDatabase.getInstance().reference

    /**
     * Attempts to login by verifying email and password in Firebase.
     * Saves uid to SharedPreferences if successful.
     */
    suspend fun login(email: String, password: String): Boolean = suspendCancellableCoroutine { cont ->
        val hashedPassword = password.md5()
        db.child("users").orderByChild("email").equalTo(email)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val userPassword = child.child("password").getValue(String::class.java)
                        val uid = child.key
                        if (userPassword == hashedPassword && uid != null) {
                            GlobalScope.launch {
                                UserSessionManager.saveUserUid(context, uid)
                            }
                            cont.resume(true)
                            return@addOnSuccessListener
                        }
                    }
                }
                cont.resume(false)
            }
            .addOnFailureListener {
                cont.resume(false)
            }
    }

    /**
     * Checks if a user with the given email exists in Firebase.
     */
    suspend fun getUserByEmail(email: String): Boolean = suspendCancellableCoroutine { cont ->
        db.child("users").orderByChild("email").equalTo(email)
            .get()
            .addOnSuccessListener { snapshot ->
                cont.resume(snapshot.exists())
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

    /**
     * Uploads the given avatar bitmap to Firebase Storage and returns the public URL.
     */
    suspend fun uploadAvatarToFirebase(userId: String, bitmap: Bitmap): String = suspendCancellableCoroutine { cont ->
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

    /**
     * Registers a user by writing a map of fields to Firebase under their UID.
     */
    suspend fun registerUser(uid: String, userMap: Map<String, Any>) {
        db.child("users").child(uid).setValue(userMap).await()
    }

    /**
     * Fetches a user’s profile by UID from Firebase.
     */
    suspend fun getUserByUid(uid: String): UserModel? = suspendCancellableCoroutine { cont ->
        db.child("users").child(uid).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val user = UserModel(
                        uid = uid,
                        email = snapshot.child("email").value as? String ?: "",
                        avatarUrl = snapshot.child("avatarUrl").value as? String ?: "",
                        nickname = snapshot.child("nickname").value as? String ?: "",
                        birthday = snapshot.child("birthday").value as? String ?: "",
                        primaryCampus = snapshot.child("campus").value as? String ?: "",
                        createdAt = snapshot.child("createdAt").value.toString().toLongOrNull() ?: 0L
                    )
                    cont.resume(user)
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }
}
