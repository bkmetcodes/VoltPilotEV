package com.bekacodes.voltpilotev.domain

import com.bekacodes.voltpilotev.ui.ChargerStopRecommendation
import kotlin.math.roundToInt

class AiChargerCoPilot {

    private val corridorChargers = listOf(
        ChargerStopRecommendation(
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
        ChargerStopRecommendation(
            id = "harris_ranch_grid",
            stationName = "GridPulse Harris Ranch",
            maxPowerKw = 350,
            distanceMiles = 52,
            highwayRoute = "I-5 N",
            availableStalls = 6,
            totalStalls = 12,
            arrivalSocPercent = 56,
            targetSocPercent = 80,
            chargeDurationMinutes = 14,
            nearbyAmenity = "Driver Lounge",
            amenityDistanceLabel = "VIP Lounge 80ft"
        ),
        ChargerStopRecommendation(
            id = "coalinga_express",
            stationName = "VoltExpress Coalinga Plaza",
            maxPowerKw = 250,
            distanceMiles = 12,
            highwayRoute = "CA-41 N",
            availableStalls = 4,
            totalStalls = 6,
            arrivalSocPercent = 73,
            targetSocPercent = 80,
            chargeDurationMinutes = 8,
            nearbyAmenity = "Artisan Bistro",
            amenityDistanceLabel = "Bistro 150ft"
        )
    )

    /**
     * Parses natural-language voice/text charger queries such as:
     * - "Find a 250kW fast charger within 40 miles with coffee nearby"
     * - "Find a 350kW ultra-fast charger within 60 miles with a lounge"
     * - "Find the closest 150kW+ charger under 15 miles with food"
     */
    fun resolveOptimalStop(
        query: String,
        currentSocPercent: Int
    ): ChargerStopRecommendation {
        val lower = query.lowercase()

        val maxMiles = Regex("""(?:within|under)\s+(\d+)\s*(?:miles|mi)""")
            .find(lower)
            ?.groupValues?.get(1)
            ?.toIntOrNull()

        val minKw = Regex("""(\d{3})\s*kw""")
            .find(lower)
            ?.groupValues?.get(1)
            ?.toIntOrNull() ?: 150

        val candidates = corridorChargers.filter { stop ->
            val withinDist = maxMiles == null || stop.distanceMiles <= maxMiles
            val meetsPower = stop.maxPowerKw >= minKw
            withinDist && meetsPower
        }

        val selected = when {
            lower.contains("lounge") -> corridorChargers.first { it.id == "harris_ranch_grid" }
            lower.contains("closest") || lower.contains("food") || (maxMiles != null && maxMiles <= 20) ->
                corridorChargers.first { it.id == "coalinga_express" }
            lower.contains("coffee") -> corridorChargers.first { it.id == "kettleman_hypervolt" }
            else -> candidates.firstOrNull() ?: corridorChargers.first()
        }

        // Dynamically calculate arrival SoC % based on ~0.42% SoC consumed per highway mile
        val socUsed = (selected.distanceMiles * 0.42).roundToInt()
        val computedArrivalSoc = (currentSocPercent - socUsed).coerceIn(8, 95)
        val targetSoc = if (computedArrivalSoc >= 75) 90 else 80
        val socAdded = (targetSoc - computedArrivalSoc).coerceAtLeast(5)

        // Estimate minutes on 350V/800V curve based on charger kW rating
        val estMinutes = ((socAdded * 1.15) * (250.0 / selected.maxPowerKw))
            .roundToInt()
            .coerceAtLeast(5)

        return selected.copy(
            arrivalSocPercent = computedArrivalSoc,
            targetSocPercent = targetSoc,
            chargeDurationMinutes = estMinutes
        )
    }
}