package com.test.project.database

import android.content.ContentValues
import android.database.Cursor

data class Restaurant(
    val id: Int? = null,
    val name: String,
    val description: String? = "Short description not available",
    val shortDescription: String? = "Description not available",
    val rating: Float? = 4.5f,
    val address: String,
    val phoneNumber: String,
){
    fun toContentValues(): ContentValues{
        return ContentValues().apply {
            put("name", name)
            put("address", address)
            put("phoneNumber", phoneNumber)
            put("description", description)
            put("shortDescription", shortDescription)
            put("rating", rating)
        }
    }

    companion object{

        fun getFromCursor(cursor: Cursor): Restaurant {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
            val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val shortDescription = cursor.getString(cursor.getColumnIndexOrThrow("shortDescription"))
            val rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"))

            return Restaurant(id, name, description, shortDescription, rating, address, phoneNumber)
        }
    }


}