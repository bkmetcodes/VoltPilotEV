package com.bekacodes.voltpilotev.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "cabin_profile")
data class CabinProfileEntity(
    @PrimaryKey val profileId: Int = 1,
    val driveModeName: String = "COMFORT",
    val driverTempF: Int = 68,
    val passengerTempF: Int = 70,
    val seatHeaterLevel: Int = 0,
    val fanSpeed: Int = 2,
    val lastAiPrompt: String = "Find a 250kW fast charger within 40 miles with coffee nearby"
)