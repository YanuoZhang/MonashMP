package com.example.monashMP.data.repository.query

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

fun buildProductFilterQuery(
    title: String = "",
    category: String = "All",
    minPrice: Float = 0f,
    maxPrice: Float = Float.MAX_VALUE,
    condition: String = "All",
    locations: List<String> = emptyList(),
    sortBy: String = "newest"
): SupportSQLiteQuery {
    val queryBuilder = StringBuilder("SELECT * FROM products WHERE 1=1")
    val args = ArrayList<Any>()

    if (title.isNotEmpty()) {
        queryBuilder.append(" AND title LIKE ?")
        args.add("%$title%")
    }
    if (category != "All") {
        queryBuilder.append(" AND category = ?")
        args.add(category)
    }
    queryBuilder.append(" AND price BETWEEN ? AND ?")
    args.add(minPrice)
    args.add(maxPrice)
    if (condition != "All") {
        queryBuilder.append(" AND condition = ?")
        args.add(condition)
    }
    if (locations.isNotEmpty()) {
        queryBuilder.append(" AND location IN (")
        locations.forEachIndexed { index, _ ->
            queryBuilder.append("?")
            if (index < locations.size - 1) queryBuilder.append(", ")
        }
        queryBuilder.append(")")
        args.addAll(locations)
    }
    when (sortBy) {
        "newest" -> queryBuilder.append(" ORDER BY createdAt DESC")
        "oldest" -> queryBuilder.append(" ORDER BY createdAt ASC")
        "price_high_to_low" -> queryBuilder.append(" ORDER BY price DESC")
        "price_low_to_high" -> queryBuilder.append(" ORDER BY price ASC")
    }
    return SimpleSQLiteQuery(queryBuilder.toString(), args.toArray())
}