package com.example.monashMP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.mapper.toEntity
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.model.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _formState = MutableStateFlow(ProductModel())
    val formState: StateFlow<ProductModel> = _formState

    private val _postSuccess = MutableStateFlow<Boolean?>(null)
    val postSuccess: StateFlow<Boolean?> = _postSuccess

    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors


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

    fun addPhoto(photoBase64: String) {
        val updatedPhotos = formState.value.photos.toMutableList().apply { add(photoBase64) }
        updateField { copy(photos = updatedPhotos) }
    }

    fun updateMeetupPoint(meetupPoint: String) {
        updateField { copy(meetupPoint = meetupPoint) }
    }

    fun updateDayPreferenceWeekdays(value: Boolean) {
        updateField { copy(dayPreferenceWeekdays = value) }
    }

    fun updateDayPreferenceWeekends(value: Boolean) {
        updateField { copy(dayPreferenceWeekends = value) }
    }

    fun updatePaymentMethodPreference(value: String) {
        updateField { copy(paymentMethodPreference = value) }
    }

    fun updateAdditionalNotes(value: String) {
        updateField { copy(additionalNotes = value) }
    }

    fun updateEmail(value: String) {
        updateField { copy(email = value) }
    }

    fun updatePhoneNum(value: String) {
        updateField { copy(phoneNum = value) }
    }

    fun updatePreferredContactMethod(value: String) {
        updateField { copy(preferredContactMethod = value) }
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

        _fieldErrors.value = errors
    }

    fun post() {
        val form = formState.value
        validateFields(form)

        if (_fieldErrors.value.isNotEmpty()) return

        viewModelScope.launch {
            val entity = form.toEntity()
            val result = repository.insertProduct(entity)
            _postSuccess.value = result > 0L
        }
    }
}

