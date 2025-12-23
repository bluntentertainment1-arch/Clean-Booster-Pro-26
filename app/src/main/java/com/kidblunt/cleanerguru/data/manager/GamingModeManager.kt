package com.kidblunt.cleanerguru.data.manager

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GamingModeState(
    val isEnabled: Boolean = false,
    val cpuBoostEnabled: Boolean = false,
    val enabledAt: Long = 0L
)

class GamingModeManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gaming_mode_prefs", Context.MODE_PRIVATE)
    
    private val _gamingModeState = MutableStateFlow(loadGamingModeState())
    val gamingModeState: StateFlow<GamingModeState> = _gamingModeState.asStateFlow()
    
    private fun loadGamingModeState(): GamingModeState {
        return GamingModeState(
            isEnabled = prefs.getBoolean("is_enabled", false),
            cpuBoostEnabled = prefs.getBoolean("cpu_boost_enabled", false),
            enabledAt = prefs.getLong("enabled_at", 0L)
        )
    }
    
    private fun saveGamingModeState(state: GamingModeState) {
        prefs.edit().apply {
            putBoolean("is_enabled", state.isEnabled)
            putBoolean("cpu_boost_enabled", state.cpuBoostEnabled)
            putLong("enabled_at", state.enabledAt)
            apply()
        }
    }
    
    fun enableGamingMode() {
        val currentTime = System.currentTimeMillis()
        val newState = _gamingModeState.value.copy(
            isEnabled = true,
            enabledAt = currentTime
        )
        _gamingModeState.value = newState
        saveGamingModeState(newState)
    }
    
    fun disableGamingMode() {
        val newState = _gamingModeState.value.copy(
            isEnabled = false,
            enabledAt = 0L
        )
        _gamingModeState.value = newState
        saveGamingModeState(newState)
    }
    
    fun toggleCpuBoost(enabled: Boolean) {
        val newState = _gamingModeState.value.copy(cpuBoostEnabled = enabled)
        _gamingModeState.value = newState
        saveGamingModeState(newState)
    }
    
    fun getActiveDuration(): Long {
        val currentState = _gamingModeState.value
        if (!currentState.isEnabled || currentState.enabledAt == 0L) {
            return 0L
        }
        
        val currentTime = System.currentTimeMillis()
        return currentTime - currentState.enabledAt
    }
    
    fun formatActiveDuration(): String {
        val durationMs = getActiveDuration()
        if (durationMs <= 0) return "Not active"
        
        val hours = durationMs / (60 * 60 * 1000)
        val minutes = (durationMs % (60 * 60 * 1000)) / (60 * 1000)
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m active"
            minutes > 0 -> "${minutes}m active"
            else -> "Less than 1m active"
        }
    }
}