package com.test.project.database

import android.content.ContentValues
import android.database.Cursor

data class Restaurant(
    val id: Int? = null,
    val name: String,
    val address: String,
    val phoneNumber: String,
){
    fun toContentValues(): ContentValues{
        return ContentValues().apply {
            put("name", name)
            put("address", address)
            put("phoneNumber", phoneNumber)
        }
    }

    companion object{

        fun getFromCursor(cursor: Cursor): Restaurant {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
            val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber"))
            return Restaurant(id, name, address, phoneNumber)
        }
    }


}