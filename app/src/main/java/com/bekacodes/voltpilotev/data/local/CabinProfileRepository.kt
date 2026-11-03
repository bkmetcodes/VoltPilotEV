package com.bekacodes.voltpilotev.data.local

import kotlinx.coroutines.flow.Flow

interface CabinProfileRepository {
    fun observeProfile(): Flow<CabinProfileEntity?>
    suspend fun getProfileSnapshot(): CabinProfileEntity?
    suspend fun saveProfile(profile: CabinProfileEntity)
}
