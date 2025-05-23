package com.example.monashMP.model

data class FilterData(
    val minPrice: Float,
    val maxPrice: Float,
    val selectedLocations: List<String>,
    val selectedCondition: String?,
    val sortBy: String
)
