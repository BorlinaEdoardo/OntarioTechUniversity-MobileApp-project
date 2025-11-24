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
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val videoUri: String? = null,
    val image1Uri: String? = null,
    val image2Uri: String? = null
){
    fun toContentValues(): ContentValues{
        return ContentValues().apply {
            put("name", name)
            put("address", address)
            put("phoneNumber", phoneNumber)
            put("description", description)
            put("shortDescription", shortDescription)
            put("rating", rating)
            put("videoUri", videoUri)
            put("image1Uri", image1Uri)
            put("image2Uri", image2Uri)
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

            val videoUri = cursor.getColumnIndex("videoUri").let {
                if (it != -1 && !cursor.isNull(it)) cursor.getString(it) else null
            }
            val image1Uri = cursor.getColumnIndex("image1Uri").let {
                if (it != -1 && !cursor.isNull(it)) cursor.getString(it) else null
            }
            val image2Uri = cursor.getColumnIndex("image2Uri").let {
                if (it != -1 && !cursor.isNull(it)) cursor.getString(it) else null
            }

            return Restaurant(id, name, description, shortDescription, rating, address, phoneNumber, 0.0, 0.0, videoUri, image1Uri, image2Uri)
        }
    }


}