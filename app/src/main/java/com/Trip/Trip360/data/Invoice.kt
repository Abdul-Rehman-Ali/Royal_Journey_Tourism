package com.Trip.Trip360.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Invoice(
    @PrimaryKey var id: Long = System.currentTimeMillis(),
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val packageName: String? = null,
    val additionalAddon: String? = null,
    val noOfAdults: Int? = null,
    val pkgPricePerAdult: Double? = null,
    val noOfKids: Int? = null,
    val pkgPricePerKid: Double? = null,
    val pickupDate: String? = null,
    val pickupTime: String? = null,
    val pickupLocation: String? = null,
    val paymentStatus: Boolean = false,
    val firebaseSync: Boolean = false,
    val webName: String,
    val currentDate: String,
    val totalPrice: Double = 0.0,
    var filePath: String? = null,
    var firestoreDocRef: String? = null
)

