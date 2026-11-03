package com.bekacodes.voltpilotev.di

import android.content.Context
import com.bekacodes.voltpilotev.data.local.CabinProfileRepository
import com.bekacodes.voltpilotev.data.local.CabinProfileRepositoryImpl
import com.bekacodes.voltpilotev.data.local.VoltPilotDao
import com.bekacodes.voltpilotev.data.local.VoltPilotDatabase
import com.bekacodes.voltpilotev.data.vhal.SimulatedVehicleHalEngine
import com.bekacodes.voltpilotev.data.vhal.VehicleHalDataSource
import com.bekacodes.voltpilotev.domain.AiChargerCoPilot
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AutomotiveModule {

    @Binds
    @Singleton
    abstract fun bindVehicleHalDataSource(
        impl: SimulatedVehicleHalEngine
    ): VehicleHalDataSource

    @Binds
    @Singleton
    abstract fun bindCabinProfileRepository(
        impl: CabinProfileRepositoryImpl
    ): CabinProfileRepository

    companion object {
        @Provides
        @Singleton
        fun provideVoltPilotDatabase(
            @ApplicationContext context: Context
        ): VoltPilotDatabase {
            return VoltPilotDatabase.getInstance(context)
        }

        @Provides
        @Singleton
        fun provideVoltPilotDao(database: VoltPilotDatabase): VoltPilotDao {
            return database.dao()
        }

        @Provides
        @Singleton
        fun provideAiChargerCoPilot(): AiChargerCoPilot {
            return AiChargerCoPilot()
        }
    }
}
