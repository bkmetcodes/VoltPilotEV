package com.bekacodes.voltpilotev.data.vhal

import com.bekacodes.voltpilotev.ui.DriveMode
import com.bekacodes.voltpilotev.ui.GearState
import kotlinx.coroutines.flow.Flow

interface VehicleHalDataSource {
    /**
     * Streams continuous VHAL telemetry ticks reacting to active gear, drive mode,
     * and thermal battery preconditioning state.
     */
    fun observeTelemetry(
        gearProvider: () -> GearState,
        driveModeProvider: () -> DriveMode,
        preconditioningProvider: () -> Boolean
    ): Flow<VhalTelemetryTick>
}
