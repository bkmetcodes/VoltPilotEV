package com.bekacodes.voltpilotev.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(
    entities = [CabinProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VoltPilotDatabase : RoomDatabase() {
    abstract fun dao(): VoltPilotDao

    companion object {
        @Volatile
        private var INSTANCE: VoltPilotDatabase? = null

        fun getInstance(context: Context): VoltPilotDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    VoltPilotDatabase::class.java,
                    "voltpilot_automotive.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}