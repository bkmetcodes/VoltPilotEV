package com.bekacodes.voltpilotev.ui

import androidx.compose.runtime.Immutable

@Immutable
data class VoltPilotUiState(
    // Top System & Gear Bar
    val activeGear: GearState = GearState.PARK,
    val ambientTempF: Int = 68,
    val isVhalConnected: Boolean = true,
    val systemTime: String = "2:45 PM",

    // Left Column: Speed, Power & Vehicle Status
    val speedMph: Int = 0,
    val powerKw: Int = 0, // Positive = Power output, Negative = Regen braking
    val driveMode: DriveMode = DriveMode.COMFORT,
    val isVehicleSecured: Boolean = true,
    val statusSubtitle: String = "All systems nominal",

    // Left Column: Battery & Range Card
    val socPercent: Int = 78,
    val rangeMiles: Int = 242,
    val architectureVolts: Int = 350,
    val batteryTempF: Int = 74,
    val tirePressuresPsi: List<Int> = listOf(42, 42, 41, 42),
    val isPreconditioningActive: Boolean = false,

    // Right Column: AI Co-Pilot & Charger Stop
    val aiPromptText: String = "Find a 250kW fast charger within 40 miles with coffee nearby",
    val presetVoiceCommands: List<String> = listOf(
        "Find a 250kW fast charger within 40 miles with coffee nearby",
        "Find a 350kW ultra-fast charger within 60 miles with a lounge",
        "Find the closest 150kW+ charger under 15 miles with food"
    ),
    val isPromptDialogOpen: Boolean = false,
    val promptInputBuffer: String = "",
    val recommendedStop: ChargerStopRecommendation = ChargerStopRecommendation(
        id = "kettleman_hypervolt",
        stationName = "HyperVolt Kettleman City",
        maxPowerKw = 350,
        distanceMiles = 28,
        highwayRoute = "I-5 N",
        availableStalls = 8,
        totalStalls = 10,
        arrivalSocPercent = 66,
        targetSocPercent = 80,
        chargeDurationMinutes = 11,
        nearbyAmenity = "Blue Bottle",
        amenityDistanceLabel = "Blue Bottle 120ft"
    ),
    val isNavigationActive: Boolean = false,

    // Bottom Dock: Dual-Zone HVAC & Quick Controls
    val driverTempF: Int = 68,
    val passengerTempF: Int = 70,
    val seatHeaterLevel: Int = 0, // 0..3
    val isDefrostOn: Boolean = false,
    val isAutoHvacOn: Boolean = true,
    val fanSpeed: Int = 2, // 1..5
    val isFrunkOpen: Boolean = false,
    val isChargePortOpen: Boolean = false
) {
    val isDriveDistractionRestricted: Boolean
        get() = activeGear == GearState.DRIVE || activeGear == GearState.REVERSE
}