package com.example.monashMP.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.data.model.FilterState
import com.example.monashMP.data.model.ProductModel
import com.example.monashMP.data.model.ProfileItem
import com.example.monashMP.data.model.ProfileItemType
import com.example.monashMP.data.model.UserModel
import com.example.monashMP.data.model.toEntity
import com.example.monashMP.data.model.toModel
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.model.FilterData
import com.example.monashMP.network.RetrofitClient
import com.example.monashMP.network.WeatherResponse
import com.example.monashMP.utils.ImageUtils.base64ToBitmap
import com.example.monashMP.utils.UserSessionManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val userUid: String
) : ViewModel() {

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    private val _filteredProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val filteredProducts: StateFlow<List<ProductModel>> = _filteredProducts

    private val _favoriteProductIds = MutableStateFlow<List<Long>>(emptyList())
    val favoriteProductIds: StateFlow<List<Long>> = _favoriteProductIds

    private val _product = MutableStateFlow<ProductModel?>(null)
    val product: StateFlow<ProductModel?> = _product

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _sellerInfo = MutableStateFlow<UserModel?>(null)
    val sellerInfo: StateFlow<UserModel?> = _sellerInfo

    private val _postedItems = MutableStateFlow<List<ProfileItem>>(emptyList())
    val postedItems: StateFlow<List<ProfileItem>> = _postedItems

    private val _savedItems = MutableStateFlow<List<ProfileItem>>(emptyList())
    val savedItems: StateFlow<List<ProfileItem>> = _savedItems

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    private val _formState = MutableStateFlow(ProductModel())
    val formState: StateFlow<ProductModel> = _formState

    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _postSuccess = MutableStateFlow(false)
    val postSuccess: StateFlow<Boolean> = _postSuccess

    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet

    val meetupPointDatasource: List<String>
        get() = when (formState.value.location) {
            "Clayton" -> listOf("LTB", "SML Library", "Monash sport", "Monash CLUB", "Bus stop", "Learning Village")
            "Caulfield" -> listOf("Building H", "Monash sport", "Library")
            else -> emptyList()
        }

    private var _draftSaved = false


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

    init {
        loadFilteredProducts()
        loadFavoriteIds()
    }

    fun updateQuery(query: String) = _filterState.update { it.copy(query = query) }
    fun updateCategory(category: String) = _filterState.update { it.copy(category = category) }
    fun updateFilterData(filterData: FilterData) = _filterState.update {
        it.copy(
            minPrice = filterData.minPrice,
            maxPrice = filterData.maxPrice,
            locations = filterData.selectedLocations,
            condition = filterData.selectedCondition ?: "All",
            sortBy = filterData.sortBy
        )
    }
    fun resetFilter() = _filterState.update {
        it.copy(minPrice = 0f, maxPrice = Float.MAX_VALUE, locations = emptyList(), condition = "All")
    }

    fun toggleFilterSheet() {
        _showFilterSheet.value = !_showFilterSheet.value
    }

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

    fun loadFavoriteIds() {
        viewModelScope.launch {
            _favoriteProductIds.value = productRepository.getFavoriteProductIds(userUid)
        }
    }

    fun fetchProduct(productId: Long) {
        viewModelScope.launch {
            val result = productRepository.getProductById(productId)
            _product.value = result
            _isFavorite.value = result?.let { productRepository.isFavorite(userUid, it.productId) } ?: false
            val sellerUid = result?.sellerUid
            if (!sellerUid.isNullOrBlank()) {
                _sellerInfo.value = productRepository.getSellerInfo(sellerUid)
            }
        }
    }

    fun toggleFavorite(userUid: String, productId: Long) {
        viewModelScope.launch {
            val isFav = productRepository.isFavorite(userUid, productId)
            if (isFav) {
                productRepository.removeFavorite(userUid, productId)
            } else {
                productRepository.addFavorite(userUid, productId)
            }
            _isFavorite.value = !isFav
            _favoriteProductIds.value = productRepository.getFavoriteProductIds(userUid)
        }
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
                Log.d("finalProduct", finalProduct.toString())
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

    fun loadProductForDetail(productId: Long) {
        viewModelScope.launch {
            val product = productRepository.getProductById(productId)
            _formState.value = product ?: ProductModel()
        }
    }

    fun loadMyProducts() {
        viewModelScope.launch {
            val posted = productRepository.getUserProducts(userUid)
            val postedItems = posted.map {
                ProfileItem(
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
                ProfileItem(
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

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
            _postedItems.update { it.filterNot { product -> product.id == productId } }
        }
    }

    fun deleteDraftProduct(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteDraftProductById(productId)
            _postedItems.update { it.filterNot { it.id == productId } }
        }
    }

    fun incrementViewCount(productId: Long) {
        viewModelScope.launch {
            productRepository.incrementAndGetViewCount(productId)
        }
    }

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

    fun getLatLng(location: String, meetupPoint: String): LatLng? {
        return campusLocationMap[location]?.get(meetupPoint)
    }

    fun checkFavoriteStatus(userUid: String, productId: Long) {
        viewModelScope.launch {
            _isFavorite.value = productRepository.isFavorite(userUid, productId)
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

    private var tempProductId: Long? = null
    fun getTempProductId(): Long {
        if (tempProductId == null) {
            tempProductId = System.currentTimeMillis()
        }
        return tempProductId!!
    }

    suspend fun uploadAndGetPhotoUrl(productId: Long, index: Int, bitmap: Bitmap): String {
        val url = productRepository.uploadProductImage(productId, index, bitmap)
        return url
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
            _draftSaved = true
            productRepository.insertDraftProduct(draftEntity)
            Toast.makeText(context, "The draft has been saved.", Toast.LENGTH_SHORT).show()
        }
    }

    fun cleanUpProductImageFolder() {
        if (_draftSaved || postSuccess.value) return
        val id = tempProductId ?: return
        viewModelScope.launch {
            productRepository.deleteProductImageFolder(id)
            Log.d("Cleanup", "Deleted folder for productId: $id")
        }
    }

}