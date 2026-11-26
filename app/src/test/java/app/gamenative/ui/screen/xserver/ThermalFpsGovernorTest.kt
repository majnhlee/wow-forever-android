package app.gamenative.ui.screen.xserver

import android.os.PowerManager
import org.junit.Assert.assertEquals
import org.junit.Test

class ThermalFpsGovernorTest {
    @Test
    fun coolDeviceKeepsThePlayersLimit() {
        assertEquals(60, ThermalFpsGovernor.limit(60, PowerManager.THERMAL_STATUS_NONE))
        assertEquals(60, ThermalFpsGovernor.limit(60, PowerManager.THERMAL_STATUS_LIGHT))
        assertEquals(0, ThermalFpsGovernor.limit(0, PowerManager.THERMAL_STATUS_NONE))
    }

    @Test
    fun moderateHeatCapsAt40() {
        assertEquals(40, ThermalFpsGovernor.limit(60, PowerManager.THERMAL_STATUS_MODERATE))
    }

    @Test
    fun severeHeatAndWorseCapAt30() {
        listOf(
            PowerManager.THERMAL_STATUS_SEVERE,
            PowerManager.THERMAL_STATUS_CRITICAL,
            PowerManager.THERMAL_STATUS_EMERGENCY,
            PowerManager.THERMAL_STATUS_SHUTDOWN,
        ).forEach { assertEquals(30, ThermalFpsGovernor.limit(60, it)) }
    }

    @Test
    fun neverRaisesALimitThePlayerSetBelowTheCap() {
        assertEquals(30, ThermalFpsGovernor.limit(30, PowerManager.THERMAL_STATUS_MODERATE))
        assertEquals(24, ThermalFpsGovernor.limit(24, PowerManager.THERMAL_STATUS_SEVERE))
    }

    @Test
    fun hotDeviceCapsAnUnlimitedPlayer() {
        assertEquals(40, ThermalFpsGovernor.limit(0, PowerManager.THERMAL_STATUS_MODERATE))
        assertEquals(30, ThermalFpsGovernor.limit(0, PowerManager.THERMAL_STATUS_SEVERE))
    }
}
