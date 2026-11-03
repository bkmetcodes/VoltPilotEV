package com.bekacodes.voltpilotev.ui

import androidx.lifecycle.viewModelScope
import com.bekacodes.voltpilotev.data.local.CabinProfileEntity
import com.bekacodes.voltpilotev.data.local.CabinProfileRepository
import com.bekacodes.voltpilotev.data.vhal.SimulatedVehicleHalEngine
import com.bekacodes.voltpilotev.data.vhal.VehicleHalDataSource
import com.bekacodes.voltpilotev.domain.AiChargerCoPilot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VoltPilotViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeCabinProfileRepository
    private lateinit var vhalDataSource: VehicleHalDataSource
    private lateinit var aiCoPilot: AiChargerCoPilot

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeCabinProfileRepository()
        vhalDataSource = SimulatedVehicleHalEngine()
        aiCoPilot = AiChargerCoPilot()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `shifting from PARK to DRIVE enforces AAOS safety interlocks and closes Frunk and Charge Port`() = runTest(testDispatcher) {
        val viewModel = VoltPilotViewModel(fakeRepository, vhalDataSource, aiCoPilot)
        runCurrent()

        // 1. In PARK, open Frunk, Charge Port, and AI keyboard dialog
        viewModel.onIntent(VoltPilotIntent.ToggleFrunk)
        viewModel.onIntent(VoltPilotIntent.ToggleChargePort)
        viewModel.onIntent(VoltPilotIntent.TogglePromptDialog(true))
        runCurrent()

        assertTrue(viewModel.uiState.value.isFrunkOpen)
        assertTrue(viewModel.uiState.value.isChargePortOpen)
        assertTrue(viewModel.uiState.value.isPromptDialogOpen)

        // 2. Shift into DRIVE -> All physical hatches and keyboard dialogs must auto-close immediately
        viewModel.onIntent(VoltPilotIntent.SelectGear(GearState.DRIVE))
        advanceTimeBy(500L)
        runCurrent()

        val driveState = viewModel.uiState.value
        assertEquals(GearState.DRIVE, driveState.activeGear)
        assertTrue(driveState.isDriveDistractionRestricted)
        assertFalse(driveState.isFrunkOpen)
        assertFalse(driveState.isChargePortOpen)
        assertFalse(driveState.isPromptDialogOpen)
        assertTrue(driveState.speedMph > 0)

        // 3. Attempting to open Frunk while in DRIVE must be blocked by safety interlock
        viewModel.onIntent(VoltPilotIntent.ToggleFrunk)
        runCurrent()
        assertFalse(viewModel.uiState.value.isFrunkOpen)

        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `AiChargerCoPilot resolves Kettleman City 350kW stop for coffee query and computes arrival SoC`() {
        val stop = aiCoPilot.resolveOptimalStop(
            query = "Find a 250kW fast charger within 40 miles with coffee nearby",
            currentSocPercent = 78
        )

        assertEquals("HyperVolt Kettleman City", stop.stationName)
        assertEquals(350, stop.maxPowerKw)
        assertEquals("Blue Bottle", stop.nearbyAmenity)
        assertEquals(66, stop.arrivalSocPercent)
    }

    @Test
    fun `adjusting Dual-Zone HVAC temperatures and DriveMode persists to Room cabin profile`() = runTest(testDispatcher) {
        val viewModel = VoltPilotViewModel(fakeRepository, vhalDataSource, aiCoPilot)
        runCurrent()

        viewModel.onIntent(VoltPilotIntent.AdjustDriverTemp(2)) // 68 -> 70
        viewModel.onIntent(VoltPilotIntent.AdjustPassengerTemp(-1)) // 70 -> 69
        viewModel.onIntent(VoltPilotIntent.SelectDriveMode(DriveMode.SPORT))
        runCurrent()

        val savedProfile = fakeRepository.getProfileSnapshot()
        assertNotNull(savedProfile)
        assertEquals(70, savedProfile?.driverTempF)
        assertEquals(69, savedProfile?.passengerTempF)
        assertEquals("SPORT", savedProfile?.driveModeName)

        viewModel.viewModelScope.cancel()
    }
}

private class FakeCabinProfileRepository : CabinProfileRepository {
    private val profileFlow = MutableStateFlow<CabinProfileEntity?>(null)

    override fun observeProfile(): Flow<CabinProfileEntity?> = profileFlow

    override suspend fun getProfileSnapshot(): CabinProfileEntity? = profileFlow.value

    override suspend fun saveProfile(profile: CabinProfileEntity) {
        profileFlow.value = profile
    }
}
