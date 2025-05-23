package com.example.monashMP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.data.repository.UserRepository

/**
 * A centralized ViewModel factory responsible for creating ViewModel instances
 * with the necessary constructor dependencies.
 *
 * This avoids the use of reflection and allows us to inject repositories and userUid
 * where needed (e.g., into ProductViewModel or AuthViewModel).
 */
class AppViewModelFactory(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val userUid: String // Current logged-in user's UID
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the given ViewModel class, injecting required dependencies.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return An instance of the ViewModel with dependencies provided.
     * @throws IllegalArgumentException if the ViewModel class is not recognized.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // For ProductViewModel, inject ProductRepository and current user's UID
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> {
                ProductViewModel(productRepository, userUid) as T
            }
            // For AuthViewModel, inject UserRepository only
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.simpleName}")
        }
    }
}
