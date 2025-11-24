package com.test.project.database

class Order (
    val orderId: Int? = null,
    val userId: Int,
    val restaurantId: Int,
){
    fun toContentValues(): android.content.ContentValues {
        return android.content.ContentValues().apply {
            put("userId", userId)
            put("restaurantId", restaurantId)
        }
    }

    companion object {
        fun getFromCursor(cursor: android.database.Cursor): Order {
            val orderId = cursor.getInt(cursor.getColumnIndexOrThrow("orderId"))
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"))
            val restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow("restaurantId"))

            return Order(orderId, userId, restaurantId)
        }
    }
}