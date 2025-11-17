package com.test.project.database

data class Dish(
    val id: Int? = null,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null
) {
    fun toContentValues(): android.content.ContentValues {
        return android.content.ContentValues().apply {
            put("id", id)
            put("name", name)
            put("description", description)
            put("price", price)
            put("imageUrl", imageUrl)
        }
    }

    companion object {
        fun getFromCursor(cursor: android.database.Cursor): Dish {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
            val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))

            return Dish(id, name, description, price, imageUrl)
        }
    }
}
