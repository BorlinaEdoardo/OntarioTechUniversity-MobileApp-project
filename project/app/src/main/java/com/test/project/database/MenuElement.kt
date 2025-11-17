package com.test.project.database

data class MenuElement (
    val dishId: Int,
    val restaurantId: Int,
) {
    fun toContentValues(): android.content.ContentValues {
        return android.content.ContentValues().apply {
            put("dishId", dishId)
            put("restaurantId", restaurantId)
        }
    }

    companion object {
        fun getFromCursor(cursor: android.database.Cursor): MenuElement {
            val dishId = cursor.getInt(cursor.getColumnIndexOrThrow("dishId"))
            val restaurantId = cursor.getInt(cursor.getColumnIndexOrThrow("restaurantId"))

            return MenuElement(dishId, restaurantId)
        }
    }
}