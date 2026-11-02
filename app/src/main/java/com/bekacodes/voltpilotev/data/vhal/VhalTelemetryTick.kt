package com.bekacodes.voltpilotev.data.vhal

data class VhalTelemetryTick(
    val speedMph: Int,
    val powerKw: Int,
    val socPercent: Int,
    val rangeMiles: Int,
    val batteryTempF: Int,
    val tirePressuresPsi: List<Int>
)