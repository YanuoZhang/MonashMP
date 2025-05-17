package com.example.monashMP.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.mapper.toEntity
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.model.ProductModel
import com.example.monashMP.utils.isValidAustralianPhone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: ProductRepository
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
        _formState.update { current ->
            val updated = current.update()
            validateFields(updated)
            updated
        }
    }

    fun updateTextField(field: String, value: String) {
        updateField {
            when (field) {
                "title" -> copy(title = value)
                "desc" -> copy(desc = value)
                "price" -> copy(price = value.toFloatOrNull() ?: 0f)
                "category" -> copy(category = value)
                "condition" -> copy(condition = value)
                "location" -> copy(location = value)
                "meetupPoint" -> copy(meetupPoint = value)
                "additionalNotes" -> copy(additionalNotes = value)
                "email" -> copy(email = value)
                "phoneNum" -> copy(phoneNum = value)
                "paymentMethodPreference" -> copy(paymentMethodPreference = value)
                "preferredContactMethod" -> copy(preferredContactMethod = value)
                "dayPreferenceWeekdays" -> copy(dayPreferenceWeekdays = value.toBooleanStrictOrNull() ?: false)
                "dayPreferenceWeekends" -> copy(dayPreferenceWeekends = value.toBooleanStrictOrNull() ?: false)
                else -> this
            }
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
        if (form.preferredContactMethod == "Both") {
            if (form.email.isBlank()) errors["email"] = "Email required"
            if (form.phoneNum.isBlank()) errors["phone"] = "Phone required"
        }

        if (form.phoneNum.isNotBlank() && !form.phoneNum.isValidAustralianPhone()) {
            errors["phone"] = "Must be a valid Australian phone number"
        }

        _fieldErrors.value = errors
    }

    fun postProduct() {

        val form = formState.value
        validateFields(form)
        if (_fieldErrors.value.isNotEmpty()) return
        Log.d("form", form.toString())
        viewModelScope.launch {
            try {
                _isPosting.value = true
                val entity = form.toEntity()
                val result = repository.insertProduct(entity)
                Log.d("result", result.toString())
                _postSuccess.value = result > 0L
            } catch (e: Exception) {
                Log.e("PostViewModel", "Upload failed", e)
                _postSuccess.value = false
            } finally {
                _isPosting.value = false
            }
        }
    }
}
