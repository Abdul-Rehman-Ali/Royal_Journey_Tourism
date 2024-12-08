package com.RoyalJourneyTourism.RJT.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int,
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
    val paymentStatus: Boolean? = null,
    val firebaseSync: Boolean = false
)
