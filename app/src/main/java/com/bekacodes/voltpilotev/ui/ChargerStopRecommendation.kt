package com.bekacodes.voltpilotev.ui

import androidx.compose.runtime.Immutable

@Immutable
data class ChargerStopRecommendation(
    val id: String,
    val stationName: String,
    val maxPowerKw: Int,
    val distanceMiles: Int,
    val highwayRoute: String,
    val availableStalls: Int,
    val totalStalls: Int,
    val arrivalSocPercent: Int,
    val targetSocPercent: Int,
    val chargeDurationMinutes: Int,
    val nearbyAmenity: String,
    val amenityDistanceLabel: String
)