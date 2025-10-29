package com.test.project.database

data class User (
    val id: Int? = null,
    val name: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
)