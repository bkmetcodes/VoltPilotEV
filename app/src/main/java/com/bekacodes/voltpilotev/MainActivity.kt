package com.bekacodes.voltpilotev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bekacodes.voltpilotev.data.local.VoltPilotDatabase
import com.bekacodes.voltpilotev.ui.VoltPilotCockpitScreen
import com.bekacodes.voltpilotev.ui.VoltPilotViewModel

class MainActivity : ComponentActivity() {

    private val database by lazy { VoltPilotDatabase.getInstance(applicationContext) }
    private val viewModel: VoltPilotViewModel by viewModels {
        VoltPilotViewModel.provideFactory(database.dao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                VoltPilotCockpitScreen(
                    uiState = uiState,
                    onIntent = viewModel::onIntent
                )
            }
        }
    }
}