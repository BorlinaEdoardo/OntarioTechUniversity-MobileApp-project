package com.test.project.database

import android.content.ContentValues
import android.database.Cursor

data class User (
    val id: Int? = null,
    val name: String,
    val email: String,
    val passwordHash: String? = null,
    val salt: String? = null,
){
    fun toContentValues(): ContentValues {
        return ContentValues().apply {
            put("name", name)
            put("email", email)
            put("passwordHash", passwordHash)
            put("salt", salt)
        }
    }

    companion object {
        fun getFromCursor(cursor: Cursor): User {
            return User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow("passwordHash")),
                salt = cursor.getString(cursor.getColumnIndexOrThrow("salt"))
            )
        }
    }
}