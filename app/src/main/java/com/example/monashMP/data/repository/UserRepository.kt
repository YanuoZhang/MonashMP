package com.example.monashMP.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.monashMP.model.UserModel
import com.example.monashMP.utils.UserSessionManager
import com.example.monashMP.utils.md5
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resumeWithException

class UserRepository(private val context: Context) {

    suspend fun login(email: String, password: String): Boolean = suspendCancellableCoroutine { cont ->
        val ref = Firebase.database.reference.child("users")
        val hashedPassword = password.md5()

        ref.orderByChild("email").equalTo(email)
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
                            cont.resume(true) {}
                            return@addOnSuccessListener
                        }
                    }
                }
                cont.resume(false) {}
            }
            .addOnFailureListener {
                cont.resume(false) {}
            }
    }

    suspend fun getUserByEmail(email: String): Boolean = suspendCancellableCoroutine { cont ->
        val ref = Firebase.database.reference.child("users")

        ref.orderByChild("email").equalTo(email)
            .get()
            .addOnSuccessListener { snapshot ->
                cont.resume(snapshot.exists()) {}
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }

    suspend fun uploadAvatarToFirebase(userId: String, bitmap: Bitmap): String =
        suspendCancellableCoroutine { cont ->
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
                    cont.resume(uri.toString()) {}
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }

    suspend fun registerUser(uid: String, userMap: Map<String, Any>) {
        Firebase.database.reference.child("users").child(uid).setValue(userMap).await()
    }

    suspend fun getUserByUid(uid: String): UserModel? = suspendCancellableCoroutine { cont ->
        val ref = Firebase.database.reference.child("users").child(uid)
        Log.d("userid", uid)
        ref.get()
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
                    cont.resume(user) {}
                } else {
                    cont.resume(null) {}
                }
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }



}
