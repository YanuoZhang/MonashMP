package com.example.monashMP.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.model.FilterData
import com.example.monashMP.model.FilterState
import com.example.monashMP.model.ProductModel
import com.example.monashMP.model.ProfileItemModel
import com.example.monashMP.model.ProfileItemType
import com.example.monashMP.model.UserModel
import com.example.monashMP.model.toEntity
import com.example.monashMP.model.toModel
import com.example.monashMP.network.RetrofitClient
import com.example.monashMP.network.WeatherResponse
import com.example.monashMP.utils.ImageUtils.base64ToBitmap
import com.example.monashMP.utils.UserSessionManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val userUid: String
) : ViewModel() {

    // Holds the current filter state (query, category, price range, etc.)
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    // Stores the filtered product list according to current filters
    private val _filteredProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val filteredProducts: StateFlow<List<ProductModel>> = _filteredProducts

    // Stores list of product IDs marked as favorite by the current user
    private val _favoriteProductIds = MutableStateFlow<List<Long>>(emptyList())
    val favoriteProductIds: StateFlow<List<Long>> = _favoriteProductIds

    // Holds the detail of the currently selected product
    private val _product = MutableStateFlow<ProductModel?>(null)
    val product: StateFlow<ProductModel?> = _product

    // Indicates whether the current product is a favorite
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    // Holds the seller's user info for the currently selected product
    private val _sellerInfo = MutableStateFlow<UserModel?>(null)
    val sellerInfo: StateFlow<UserModel?> = _sellerInfo

    // List of all items (posted and drafts) by current user
    private val _postedItems = MutableStateFlow<List<ProfileItemModel>>(emptyList())
    val postedItems: StateFlow<List<ProfileItemModel>> = _postedItems

    // List of items saved by the current user
    private val _savedItems = MutableStateFlow<List<ProfileItemModel>>(emptyList())
    val savedItems: StateFlow<List<ProfileItemModel>> = _savedItems

    // Holds the current weather info for a selected location
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    // Holds the current form state for product creation or editing
    private val _formState = MutableStateFlow(ProductModel())
    val formState: StateFlow<ProductModel> = _formState

    // Stores field-level validation error messages
    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    // Indicates if the product is currently being posted
    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    // Indicates whether the last post action was successful
    private val _postSuccess = MutableStateFlow(false)
    val postSuccess: StateFlow<Boolean> = _postSuccess

    // Controls the visibility of the bottom sheet filter UI
    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet

    // Emits a message when the favorite status changes
    private val _favoriteMessage = MutableSharedFlow<String>()
    val favoriteMessage = _favoriteMessage

    // Predefined campus locations and corresponding map points
    val campusLocationMap: Map<String, Map<String, LatLng>> = mapOf(
        "Clayton" to mapOf(
            "Monash Bus Loop" to LatLng(-37.9140, 145.13155),
            "Monash Sport (Clayton)" to LatLng(-37.9126116, 145.1372622),
            "Alexander Theatre" to LatLng(-37.91376, 145.13321),
            "Sir Louis Matheson Library" to LatLng(-37.9105, 145.1342),
            "Campus Centre" to LatLng(-37.9110, 145.1349),
            "LTB (Learning and Teaching Building)" to LatLng(-37.9138847, 145.132576)
        ),
        "Caulfield" to mapOf(
            "Caulfield Library" to LatLng(-37.8768, 145.0455),
            "Building H" to LatLng(-37.8770, 145.0450),
            "MUMA" to LatLng(-37.8764, 145.0460),
            "Monash Sport (Caulfield)" to LatLng(-37.87725, 145.04494)
        )
    )

    // Provides available meetup points based on current selected location
    val meetupPointDatasource: List<String>
        get() = campusLocationMap[formState.value.location]?.keys?.toList() ?: emptyList()

    // Temporary flag used to prevent deletion of drafts after save/post
    private var _draftSaved = false

    // Temporary productId used for new drafts
    private var tempProductId: Long? = null

    // Load list of favorite product IDs
    fun loadFavoriteIds() {
        viewModelScope.launch {
            _favoriteProductIds.value = productRepository.getFavoriteProductIds(userUid)
        }
    }

    // Toggle product favorite status for current user
    fun toggleFavorite(userUid: String, productId: Long) {
        viewModelScope.launch {
            val isFav = productRepository.isFavorite(userUid, productId)
            if (isFav) {
                productRepository.removeFavorite(userUid, productId)
                _favoriteMessage.emit("Removed from favorites")
            } else {
                productRepository.addFavorite(userUid, productId)
                _favoriteMessage.emit("Added to favorites")
            }

            _isFavorite.value = !isFav
            _favoriteProductIds.value = productRepository.getFavoriteProductIds(userUid)
        }
    }

    // Check favorite status for specific product
    fun checkFavoriteStatus(userUid: String, productId: Long) {
        viewModelScope.launch {
            _isFavorite.value = productRepository.isFavorite(userUid, productId)
        }
    }

    // Fetch product and seller info for detail page
    fun fetchProduct(productId: Long) {
        viewModelScope.launch {
            val result = productRepository.getProductById(productId)
            _product.value = result
            _isFavorite.value = result?.let { productRepository.isFavorite(userUid, it.productId) } == true
            val sellerUid = result?.sellerUid
            if (!sellerUid.isNullOrBlank()) {
                _sellerInfo.value = productRepository.getSellerInfo(sellerUid)
            }
        }
    }

    // Fetch product form state for editing
    fun loadProductForDetail(productId: Long) {
        viewModelScope.launch {
            val product = productRepository.getProductById(productId)
            _formState.value = product ?: ProductModel()
        }
    }


    // Load current user profile info
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

    // Fetch current weather info using lat/lng
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

    // Generic field updater using lambda
    fun updateField(update: ProductModel.() -> ProductModel) {
        _formState.update { it.update() }
    }

    // Field update by name string (used in UI text input)
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

    // Validate individual fields (triggered per field)
    fun validateField(field: String, value: String) {
        val currentErrors = _fieldErrors.value.toMutableMap()

        when (field) {
            "photos" -> {
                val error = validatePhotos(formState.value.photos)
                if (error != null) currentErrors["photos"] = error else currentErrors.remove("photos")
            }
            "title" -> {
                val error = validateTitle(value)
                if (error != null) currentErrors["title"] = error else currentErrors.remove("title")
            }
            "desc" -> {
                val error = validateDesc(value)
                if (error != null) currentErrors["desc"] = error else currentErrors.remove("desc")
            }
            "price" -> {
                val parsedPrice = value.toFloatOrNull() ?: 0f
                val error = validatePrice(parsedPrice)
                if (error != null) currentErrors["price"] = error else currentErrors.remove("price")
            }
            "category" -> {
                val error = validateCategory(value)
                if (error != null) currentErrors["category"] = error else currentErrors.remove("category")
            }
            "condition" -> {
                val error = validateCondition(value)
                if (error != null) currentErrors["condition"] = error else currentErrors.remove("condition")
            }
            "location" -> {
                val error = validateLocation(value)
                if (error != null) currentErrors["location"] = error else currentErrors.remove("location")
            }
        }

        _fieldErrors.value = currentErrors
    }

    // Validate individual fields (pure functions)
    fun validatePhotos(photos: List<String>): String? =
        if (photos.isEmpty()) "At least one photo is required" else null

    fun validateTitle(title: String): String? =
        if (title.isBlank()) "Title is required" else null

    fun validateDesc(desc: String): String? =
        if (desc.isBlank()) "Description is required" else null

    fun validatePrice(price: Float): String? =
        if (price <= 0f) "Price must be greater than 0" else null

    fun validateCategory(category: String): String? =
        if (category.isBlank()) "Category is required" else null

    fun validateCondition(condition: String): String? =
        if (condition.isBlank()) "Condition is required" else null

    fun validateLocation(location: String): String? =
        if (location.isBlank()) "Location is required" else null

    // Post a product to Firebase, uploading photos if needed
    fun postProduct() {
        val form = formState.value
        val productId = getTempProductId()
        val updatedForm = form.copy(productId = productId, sellerUid = userUid)

        validateField("photos", "")
        validateField("title", form.title)
        validateField("desc", form.desc)
        validateField("price", form.price.toString())
        validateField("category", form.category)
        validateField("condition", form.condition)
        validateField("location", form.location)

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
                val result = productRepository.insertProduct(finalProduct)
                _postSuccess.value = result

                if (result) {
                    productRepository.deleteLocalDraftIfExists(productId)
                }

            } catch (e: Exception) {
                _postSuccess.value = false
            } finally {
                _isPosting.value = false
            }
        }
    }

    // Upload a bitmap and return URL (used internally)
    suspend fun uploadAndGetPhotoUrl(productId: Long, index: Int, bitmap: Bitmap): String {
        val url = productRepository.uploadProductImage(productId, index, bitmap)
        return url
    }

    // Add photo (as base64) to form state
    fun addPhoto(photoBase64: String) {
        val updatedPhotos = formState.value.photos.toMutableList().apply { add(photoBase64) }
        updateField { copy(photos = updatedPhotos) }
    }


    // Remove photo from form and cloud storage
    fun removePhoto(url: String) {
        _formState.update {
            val updated = it.photos.toMutableList().apply { remove(url) }
            it.copy(photos = updated)
        }

        viewModelScope.launch {
            productRepository.deleteImageFromStorage(url)
        }
    }


    // Save or update draft locally
    fun saveDraft(context: Context, productId: Long? = null) {
        viewModelScope.launch {
            if(productId != null)
            {
                val draftEntity = formState.value.toEntity(isDraft = true)
                productRepository.updateLocalDraft(draftEntity)
                Toast.makeText(context, "The draft has been updated.", Toast.LENGTH_SHORT).show()
            } else {
                val sellerUid = UserSessionManager.getUserUid(context) ?: return@launch
                val form = formState.value.copy(
                    productId = getTempProductId(),
                    sellerUid = sellerUid
                )
                val draftEntity = form.toEntity(isDraft = true)
                _draftSaved = true
                productRepository.insertDraftProduct(draftEntity)
                Toast.makeText(context, "The draft has been saved.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Load specific draft for editing
    fun getDraftInfo(productId: Long)
    {
        viewModelScope.launch {
            val draft = productRepository.getDraftByProductId(productId)
            _formState.value = draft.toModel()
        }
    }

    // Clean up image folder if post not successful
    fun cleanUpProductImageFolder() {
        if (_draftSaved || postSuccess.value) return
        val id = tempProductId ?: return
        viewModelScope.launch {
            productRepository.deleteProductImageFolder(id)
        }
    }

    // Load posted and draft products for profile page
    fun loadMyProducts() {
        viewModelScope.launch {
            val posted = productRepository.getUserProducts(userUid)
            val postedItems = posted.map {
                ProfileItemModel(
                    id = it.productId,
                    title = it.title,
                    price = "$${it.price}",
                    cover = it.photos.firstOrNull() ?: "",
                    type = ProfileItemType.Posted,
                    isDraft = false
                )
            }

            val drafts = productRepository.getUserDraftProducts(userUid)
            val draftItems = drafts.map {
                ProfileItemModel(
                    id = it.productId,
                    title = it.title,
                    price = "$${it.price}",
                    cover = it.photos.firstOrNull() ?: "",
                    type = ProfileItemType.Posted,
                    isDraft = true
                )
            }

            _postedItems.value = postedItems + draftItems
        }
    }

    // Load saved (favorited) products
    fun loadSavedProducts() {
        viewModelScope.launch {
            val favIds = productRepository.getFavoritesByUser(userUid).map { it.productId }
            val favProducts = favIds.mapNotNull { productRepository.getProductById(it) }
            _savedItems.value = favProducts.map {
                ProfileItemModel(
                    id = it.productId,
                    title = it.title,
                    price = "$${it.price}",
                    cover = it.photos.firstOrNull() ?: "",
                    type = ProfileItemType.Saved
                )
            }
        }
    }

    // Delete posted product
    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
            _postedItems.update { it.filterNot { product -> product.id == productId } }
        }
    }

    // Delete draft product
    fun deleteDraftProduct(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteDraftProductById(productId)
            _postedItems.update { it.filterNot { it.id == productId } }
        }
    }


    // Get LatLng from location and meetup point
    fun getLatLng(location: String, meetupPoint: String): LatLng? {
        return campusLocationMap[location]?.get(meetupPoint)
    }

    // Get formatted string for preferred meeting days
    fun buildDayPreference(product: ProductModel?): String {
        return when {
            product == null -> "--"
            product.dayPreferenceWeekdays && product.dayPreferenceWeekends -> "Weekdays & Weekends"
            product.dayPreferenceWeekdays -> "Weekdays"
            product.dayPreferenceWeekends -> "Weekends"
            else -> "--"
        }
    }

    // Increment view count for product
    fun incrementViewCount(productId: Long) {
        viewModelScope.launch {
            productRepository.incrementAndGetViewCount(productId)
        }
    }

    // Generate a unique temporary product ID (used for drafts/post)
    fun getTempProductId(): Long {
        if (tempProductId == null) {
            tempProductId = System.currentTimeMillis()
        }
        return tempProductId!!
    }

    // Update the search query string in the filter state
    fun updateQuery(query: String) = _filterState.update { it.copy(query = query) }

    // Update the selected product category in the filter state
    fun updateCategory(category: String) = _filterState.update { it.copy(category = category) }

    // Apply a full set of filter parameters (price range, location, condition, sort order)
    fun updateFilterData(filterData: FilterData) = _filterState.update {
        it.copy(
            minPrice = filterData.minPrice,
            maxPrice = filterData.maxPrice,
            locations = filterData.selectedLocations,
            condition = filterData.selectedCondition ?: "All",
            sortBy = filterData.sortBy
        )
    }

    // Reset all filter parameters to default values
    fun resetFilter() = _filterState.update {
        it.copy(minPrice = 0f, maxPrice = Float.MAX_VALUE, locations = emptyList(), condition = "All")
    }

    // Toggle the visibility of the filter bottom sheet UI
    fun toggleFilterSheet() {
        _showFilterSheet.value = !_showFilterSheet.value
    }


    // Load and apply filtered product list based on current filter state
    fun loadFilteredProducts() {
        viewModelScope.launch {
            val state = _filterState.value
            val products = productRepository.getFilteredProducts(
                title = state.query,
                category = state.category,
                minPrice = state.minPrice,
                maxPrice = state.maxPrice,
                condition = state.condition,
                locations = state.locations,
                sortBy = state.sortBy
            )
            _filteredProducts.value = products
        }
    }
}