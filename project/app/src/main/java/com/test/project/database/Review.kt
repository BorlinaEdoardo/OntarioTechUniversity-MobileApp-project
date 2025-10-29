package com.test.project.database

data class Review(
    var userId: Int,
    var restaurantId: Int = 0,
    var rating: Float = 0.0f,
    var comment: String = ""
)
