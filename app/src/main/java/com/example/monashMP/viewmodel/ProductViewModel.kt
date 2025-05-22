package com.example.monashMP.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.model.ProductModel
import com.example.monashMP.data.model.UserModel
import com.example.monashMP.data.model.toEntity
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.utils.ImageUtils.base64ToBitmap
import com.example.monashMP.utils.UserSessionManager
import com.example.monashMP.utils.isValidAustralianPhone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel (
    private val productRepository: ProductRepository,
    private val userUid: String
) : ViewModel() {

    private val _formState = MutableStateFlow(ProductModel())
    val formState: StateFlow<ProductModel> = _formState

    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _postSuccess = MutableStateFlow(false)
    val postSuccess: StateFlow<Boolean> = _postSuccess

    val meetupPointDatasource: List<String>
        get() = when (formState.value.location) {
            "Clayton" -> listOf("LTB", "SML Library", "Monash sport", "Monash CLUB", "Bus stop", "Learning Village")
            "Caulfield" -> listOf("Building H", "Monash sport", "Library")
            else -> emptyList()
        }

    fun updateField(update: ProductModel.() -> ProductModel) {
        _formState.update { it.update() }
    }

    fun updateTextField(field: String, value: String) = updateField {
        when (field) {
            "title" -> copy(title = value)
            "desc" -> copy(desc = value)
            "category" -> copy(category = value)
            "condition" -> copy(condition = value)
            "location" -> copy(location = value)
            "meetupPoint" -> copy(meetupPoint = value)
            "additionalNotes" -> copy(additionalNotes = value)
            "email" -> copy(email = value)
            "phoneNum" -> copy(phoneNum = value)
            "paymentMethodPreference" -> copy(paymentMethodPreference = value)
            "preferredContactMethod" -> copy(preferredContactMethod = value)
            "price" -> copy(price = value.toFloatOrNull() ?: 0f)
            "dayPreferenceWeekdays" -> copy(
                dayPreferenceWeekdays = value.toBooleanStrictOrNull() == true
            )
            "dayPreferenceWeekends" -> copy(
                dayPreferenceWeekends = value.toBooleanStrictOrNull() == true
            )
            else -> this
        }

    }

    fun addPhoto(photoBase64: String) {
        val updatedPhotos = formState.value.photos.toMutableList().apply { add(photoBase64) }
        updateField { copy(photos = updatedPhotos) }
    }

    private fun validateFields(form: ProductModel) {
        val errors = mutableMapOf<String, String>()
        if (form.photos.isEmpty()) errors["photos"] = "At least one photo is required"
        if (form.title.isBlank()) errors["title"] = "Title is required"
        if (form.desc.isBlank()) errors["desc"] = "Description is required"
        if (form.price <= 0f) errors["price"] = "Price must be greater than 0"
        if (form.category.isBlank()) errors["category"] = "Category is required"
        if (form.condition.isBlank()) errors["condition"] = "Condition is required"
        if (form.location.isBlank()) errors["location"] = "Location is required"
        if (form.preferredContactMethod == "Email" && form.email.isBlank()) {
            errors["email"] = "Email required"
        }
        if (form.preferredContactMethod == "Phone" && form.phoneNum.isBlank()) {
            errors["phone"] = "Phone required"
        }
        if (form.preferredContactMethod == "Both" && form.phoneNum.isBlank()) {
            errors["phone"] = "Phone required"
        }
        if (form.phoneNum.isNotBlank() && !form.phoneNum.isValidAustralianPhone()) {
            errors["phone"] = "Must be a valid Australian phone number"
        }
        _fieldErrors.value = errors
    }

    fun postProduct() {
        val form = formState.value
        val productId = getTempProductId()
        val updatedForm = form.copy(productId = productId, sellerUid = userUid)

        validateFields(updatedForm)
        if (fieldErrors.value.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _isPosting.value = true

                val photoUrls = updatedForm.photos.mapIndexedNotNull { index, entry ->
                    if (entry.startsWith("http")) {
                        entry
                    } else {
                        val bitmap = base64ToBitmap(entry)
                        bitmap?.let {
                            productRepository.uploadProductImage(productId, index, it)
                        }
                    }
                }

                val finalProduct = updatedForm.copy(photos = photoUrls)
                Log.d("finalProduct", finalProduct.toString())
                val result = productRepository.insertProduct(finalProduct)
                _postSuccess.value = result
            } catch (e: Exception) {
                _postSuccess.value = false
            } finally {
                _isPosting.value = false
            }
        }
    }

    fun buildDayPreference(product: ProductModel?): String {
        return when {
            product == null -> "--"
            product.dayPreferenceWeekdays && product.dayPreferenceWeekends -> "Weekdays & Weekends"
            product.dayPreferenceWeekdays -> "Weekdays"
            product.dayPreferenceWeekends -> "Weekends"
            else -> "--"
        }
    }

    private var tempProductId: Long? = null
    fun getTempProductId(): Long {
        if (tempProductId == null) {
            tempProductId = System.currentTimeMillis()
        }
        return tempProductId!!
    }

    suspend fun uploadAndGetPhotoUrl(productId: Long, index: Int, bitmap: Bitmap): String {
        return productRepository.uploadProductImage(productId, index, bitmap)
    }

    fun removePhoto(url: String) {
        _formState.update {
            val updated = it.photos.toMutableList().apply { remove(url) }
            it.copy(photos = updated)
        }

        viewModelScope.launch {
            productRepository.deleteImageFromStorage(url)
        }
    }

    fun saveDraft(context: Context) {
        viewModelScope.launch {
            val sellerUid = UserSessionManager.getUserUid(context) ?: return@launch
            val form = formState.value.copy(
                productId = getTempProductId(),
                sellerUid = sellerUid
            )
            val draftEntity = form.toEntity(isDraft = true)
            productRepository.insertDraftProduct(draftEntity)
            Toast.makeText(context, "The draft has been saved.", Toast.LENGTH_SHORT).show()
        }
    }

}