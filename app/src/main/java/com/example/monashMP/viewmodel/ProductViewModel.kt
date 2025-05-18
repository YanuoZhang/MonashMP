package com.example.monashMP.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.components.FilterData
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.model.FilterState
import com.example.monashMP.model.ProfileItem
import com.example.monashMP.model.ProfileItemType
import com.example.monashMP.model.UserModel
import com.example.monashMP.network.RetrofitClient
import com.example.monashMP.network.WeatherResponse
import com.example.monashMP.utils.UserSessionManager
import com.example.monashMP.utils.isValidAustralianPhone
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * A unified ViewModel handling:
 * - Product filtering and listing
 * - Product form state and posting
 * - Favorite toggle logic
 * - User profile product management (posted/saved)
 */
class ProductViewModel(
    private val productRepository: ProductRepository,
    private val userUid: String
) : ViewModel() {

    // --- Product Filtering ---

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet

    // Static list of meetup point options for transaction preferences
    val meetupPointDatasource: List<String>
        get() = when (formState.value.location) {
            "Clayton" -> listOf("LTB", "SML Library", "Monash sport", "Monash CLUB", "Bus stop", "Learning Village")
            "Caulfield" -> listOf("Building H", "Monash sport", "Library")
            else -> emptyList()
        }

    // Toggles the visibility of the filter bottom sheet.
    fun toggleFilterSheet() {
        _showFilterSheet.value = !_showFilterSheet.value
    }

    // List of products matching current filters
    val filteredProducts: StateFlow<List<ProductEntity>> = _filterState
        .flatMapLatest { state ->
            productRepository.getFilteredProducts(
                title = state.query,
                category = state.category,
                minPrice = state.minPrice,
                maxPrice = state.maxPrice,
                condition = state.condition,
                locations = state.locations,
                sortBy = state.sortBy
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorite product IDs for current user
    val favoriteProductIds: StateFlow<List<Long>> =
        productRepository.getFavoriteProductIdsFlow(userUid)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(query: String) = _filterState.update { it.copy(query = query) }
    fun updateCategory(category: String) = _filterState.update { it.copy(category = category) }
    fun updateFilterData(filterData: FilterData) = _filterState.update {
        it.copy(
            minPrice = filterData.minPrice.toFloatOrNull() ?: 0f,
            maxPrice = filterData.maxPrice.toFloatOrNull() ?: Float.MAX_VALUE,
            locations = filterData.selectedLocations,
            condition = filterData.selectedCondition ?: "All",
            sortBy = filterData.sortBy
        )
    }
    fun resetFilter() = _filterState.update {
        it.copy(minPrice = 0f, maxPrice = Float.MAX_VALUE, locations = emptyList(), condition = "All")
    }
    // --- Product Detail Display ---

    private val _product = MutableStateFlow<ProductEntity?>(null)
    val product: StateFlow<ProductEntity?> = _product

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _sellerInfo = MutableStateFlow<UserModel?>(null)
    val sellerInfo: StateFlow<UserModel?> = _sellerInfo

    // Load product by ID and fetch seller info
    fun fetchProduct(productId: Long) {
        viewModelScope.launch {
            val result = productRepository.getProductById(productId)
            _product.value = result
            val sellerUid = result?.sellerUid
            if (!sellerUid.isNullOrBlank()) {
                _sellerInfo.value = productRepository.getSellerInfo(sellerUid)
            }
        }
    }

    // Check if user has saved the product
    fun checkFavoriteStatus(userUid: String, productId: Long) {
        viewModelScope.launch {
            _isFavorite.value = productRepository.isFavorite(userUid, productId)
        }
    }
    // --- Favorite toggle ---

    fun toggleFavorite(userUid: String, productId: Long) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                productRepository.removeFavorite(userUid, productId)
            } else {
                productRepository.addFavorite(userUid, productId)
            }
            _isFavorite.value = !_isFavorite.value
        }
    }

    fun buildDayPreference(product: ProductEntity?): String {
        return when {
            product == null -> "--"
            product.dayPreferenceWeekdays && product.dayPreferenceWeekends -> "Weekdays & Weekends"
            product.dayPreferenceWeekdays -> "Weekdays"
            product.dayPreferenceWeekends -> "Weekends"
            else -> "--"
        }
    }

    // --- Product Form State (for Posting) ---

    private val _formState = MutableStateFlow(ProductEntity())
    val formState: StateFlow<ProductEntity> = _formState

    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _postSuccess = MutableStateFlow(false)
    val postSuccess: StateFlow<Boolean> = _postSuccess

    /**
     * Generic field update using ProductEntity.copy()
     */
    fun updateField(update: ProductEntity.() -> ProductEntity) {
        _formState.update { current ->
            current.update()
        }
    }

    /**
     * Update a specific text field in the form state
     */
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
            "dayPreferenceWeekdays" -> copy(dayPreferenceWeekdays = value.toBooleanStrictOrNull() ?: false)
            "dayPreferenceWeekends" -> copy(dayPreferenceWeekends = value.toBooleanStrictOrNull() ?: false)
            else -> this
        }
    }

    fun addPhoto(photoBase64: String) {
        val updatedPhotos = formState.value.photos.toMutableList().apply { add(photoBase64) }
        updateField { copy(photos = updatedPhotos) }
    }

    /**
     * Validate form fields and populate error map
     */
    private fun validateFields(form: ProductEntity) {
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

    /**
     * Post product to Room
     */
    fun postProduct() {
        val form = formState.value
        val updatedForm = form.copy(sellerUid = userUid)
        validateFields(updatedForm)
        if (_fieldErrors.value.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _isPosting.value = true
                val result = productRepository.insertProduct(updatedForm)
                _postSuccess.value = result > 0L
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Post failed", e)
                _postSuccess.value = false
            } finally {
                _isPosting.value = false
            }
        }
    }

    // --- Profile view (My Posted / Saved Items) ---

    private val _postedItems = MutableStateFlow<List<ProfileItem>>(emptyList())
    val postedItems: StateFlow<List<ProfileItem>> = _postedItems

    private val _savedItems = MutableStateFlow<List<ProfileItem>>(emptyList())
    val savedItems: StateFlow<List<ProfileItem>> = _savedItems

    fun loadMyProducts() {
        viewModelScope.launch {
            val posted = productRepository.getUserProducts(userUid)
            _postedItems.value = posted.map {
                ProfileItem(
                    id = it.productId,
                    title = it.title,
                    price = "$${it.price}",
                    cover = it.photos.firstOrNull() ?: "",
                    type = ProfileItemType.Posted
                )
            }
        }
    }

    fun loadSavedProducts() {
        viewModelScope.launch {
            val favIds = productRepository.getFavoritesByUser(userUid).map { it.productId }
            val favProducts = favIds.mapNotNull { productRepository.getProductById(it) }
            _savedItems.value = favProducts.map {
                ProfileItem(
                    id = it.productId,
                    title = it.title,
                    price = "$${it.price}",
                    cover = it.photos.firstOrNull() ?: "",
                    type = ProfileItemType.Saved
                )
            }
        }
    }

    fun deleteProduct(item: ProfileItem) {
        viewModelScope.launch {
            productRepository.deleteProduct(item.id)
            _postedItems.update { it.filterNot { product -> product.id == item.id } }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            UserSessionManager.clearSession(context)
        }
    }

    // Weather forecast data for the selected location
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    /**
     * Predefined campus location map for generating map markers
     */
    val campusLocationMap: Map<String, Map<String, LatLng>> = mapOf(
        "Clayton" to mapOf(
            "Monash Sport" to LatLng(-37.9116, 145.1340),
            "SML Library" to LatLng(-37.9110, 145.1335),
            "LTB" to LatLng(-37.9102, 145.1347),
            "Monash CLUB" to LatLng(-37.9125, 145.1329),
            "Bus stop" to LatLng(-37.9120, 145.1310),
            "Learning Village" to LatLng(-37.9107, 145.1330)
        ),
        "Caulfield" to mapOf(
            "Building H" to LatLng(-37.8770, 145.0450),
            "Monash sport" to LatLng(-37.8765, 145.0462),
            "Library" to LatLng(-37.8768, 145.0455)
        )
    )

    /**
     * Returns the LatLng for a given campus and meetup point.
     */
    fun getLatLng(location: String, meetupPoint: String): LatLng? {
        return campusLocationMap[location]?.get(meetupPoint)
    }

    /**
     * Fetch weather data for the given coordinates using OpenWeather API.
     */
    fun fetchWeather(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.weatherApi.getWeatherByLatLng(lat, lon, apiKey)
                _weather.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            try {
                val user = productRepository.getSellerInfo(userUid)
                _sellerInfo.value = user
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Failed to load user info", e)
            }
        }
    }

    /**
     * Loads a product by ID into the formState, typically for viewing (not editing).
     */
    fun loadProductForDetail(productId: Long) {
        viewModelScope.launch {
            val product = productRepository.getProductById(productId)
            _formState.value = product ?: ProductEntity()
        }
    }

    fun syncProductsWithFirebase() {
        viewModelScope.launch {
            try {
                productRepository.syncWithFirebase()
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Sync failed", e)
            }
        }
    }

}