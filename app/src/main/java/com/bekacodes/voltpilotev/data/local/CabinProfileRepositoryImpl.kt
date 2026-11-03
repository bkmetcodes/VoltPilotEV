package com.bekacodes.voltpilotev.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CabinProfileRepositoryImpl @Inject constructor(
    private val dao: VoltPilotDao
) : CabinProfileRepository {

    override fun observeProfile(): Flow<CabinProfileEntity?> = dao.observeProfile()

    override suspend fun getProfileSnapshot(): CabinProfileEntity? = dao.getProfileSnapshot()

    override suspend fun saveProfile(profile: CabinProfileEntity) = dao.saveProfile(profile)
}
