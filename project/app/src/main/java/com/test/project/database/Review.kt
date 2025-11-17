package com.test.project.database

data class Review(
    var userId: Int,
    var restaurantId: Int = 0,
    var rating: Float = 0.0f,
    var comment: String = ""
) {
    fun toContentValues(): android.content.ContentValues {
        return android.content.ContentValues().apply {
            put("userId", userId)
            put("restaurantId", restaurantId)
            put("rating", rating)
            put("comment", comment)
        }
    }

    companion object {
        fun getFromCursor(cursor: android.database.Cursor): Review {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"))
            val restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow("restaurantId"))
            val rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"))
            val comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"))

            return Review(userId, restaurantId, rating, comment)
        }
    }
}
