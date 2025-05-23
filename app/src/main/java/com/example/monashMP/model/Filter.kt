package com.example.monashMP.model

data class FilterData(
    val minPrice: Float,
    val maxPrice: Float,
    val selectedLocations: List<String>,
    val selectedCondition: String?,
    val sortBy: String
)

data class FilterState(
    val query: String = "",
    val category: String = "All",
    val minPrice: Float = 0f,
    val maxPrice: Float = Float.MAX_VALUE,
    val condition: String = "All",
    val locations: List<String> = emptyList(),
    val sortBy: String = "Newest"
)