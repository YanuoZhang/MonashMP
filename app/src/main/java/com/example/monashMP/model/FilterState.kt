package com.example.monashMP.data.model

data class FilterState(
    val query: String = "",
    val category: String = "All",
    val minPrice: Float = 0f,
    val maxPrice: Float = Float.MAX_VALUE,
    val condition: String = "All",
    val locations: List<String> = emptyList(),
    val sortBy: String = "newest"
)