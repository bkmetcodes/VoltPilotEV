package com.bekacodes.voltpilotev.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VoltPilotDao {
    @Query("SELECT * FROM cabin_profile WHERE profileId = 1 LIMIT 1")
    fun observeProfile(): Flow<CabinProfileEntity?>

    @Query("SELECT * FROM cabin_profile WHERE profileId = 1 LIMIT 1")
    suspend fun getProfileSnapshot(): CabinProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: CabinProfileEntity)
}