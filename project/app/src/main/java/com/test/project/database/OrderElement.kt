package com.test.project.database

import android.content.ContentValues

data class OrderElement(
    val orderId: Int,
    val dishId: Int,
){
    public fun toContentValues(): ContentValues{
        return ContentValues().apply{
            put("orderId", orderId)
            put("dishId", dishId)
        }
    }
    companion object{
        fun fromContentValues(contentValues: ContentValues): OrderElement{
            val orderId = contentValues.getAsInteger("orderId")
            val dishId = contentValues.getAsInteger("dishId")
            return OrderElement(orderId, dishId)
        }
    }
}