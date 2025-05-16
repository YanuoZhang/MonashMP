package com.example.monashMP.model

data class FilterState(
    val query: String = "",
    val category: String = "All",
    val minPrice: Float = 0f,
    val maxPrice: Float = 100f,
    val condition: String = "All",
    val locations: List<String> = emptyList(),
    val sortBy: String = "newest"
)