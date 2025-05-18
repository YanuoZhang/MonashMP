package com.example.monashMP.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monashMP.components.FilterData
import com.example.monashMP.data.entity.ProductEntity
import com.example.monashMP.data.repository.ProductRepository
import com.example.monashMP.data.repository.UserFavoriteRepository
import com.example.monashMP.model.FilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val userFavoriteRepository: UserFavoriteRepository,
    private val userUid: String
) : ViewModel() {
    init {
        viewModelScope.launch {
            val count = productRepository.getLocalProductCount()
            if (count == 0) {
                try {
                    val firebaseProducts = productRepository.fetchAllFromFirebase()
                    productRepository.insertAllIntoRoom(firebaseProducts)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    val favoriteProductIds: StateFlow<List<Long>> =
        userFavoriteRepository.getFavoriteProductIdsFlow(userUid)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


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
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateQuery(query: String) {
        _filterState.update { it.copy(query = query) }
    }

    fun updateCategory(category: String) {
        _filterState.update { it.copy(category = category) }
    }

    fun updateFilterData(filterData: FilterData) {
        _filterState.update {
            it.copy(
                minPrice = filterData.minPrice.toFloatOrNull() ?: 0f,
                maxPrice = filterData.maxPrice.toFloatOrNull() ?: Float.MAX_VALUE,
                locations = filterData.selectedLocations,
                condition = filterData.selectedCondition ?: "All",
                sortBy = filterData.sortBy
            )
        }
    }

    fun resetFilter() {
        _filterState.update {
            it.copy(minPrice = 0f, maxPrice = Float.MAX_VALUE, locations = emptyList(), condition = "All")
        }
    }

    var showFilterSheet = mutableStateOf(false)
    fun toggleFilterSheet() {
        showFilterSheet.value = !showFilterSheet.value
    }
}
