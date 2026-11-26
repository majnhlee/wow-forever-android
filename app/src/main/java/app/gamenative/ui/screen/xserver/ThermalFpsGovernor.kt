package app.gamenative.ui.screen.xserver

import android.os.PowerManager

/**
 * Lowers the FPS limit while Android reports the device as hot, so a fanless phone eases off before the system
 * throttles the GPU and CPU on its own, which shows up as sudden stutter. The player's own limit is never raised.
 *
 * The caps are divisors of a 120 Hz panel's refresh rate, so frames stay evenly paced on it.
 */
internal object ThermalFpsGovernor {
    internal const val MODERATE_CAP_FPS = 40
    internal const val SEVERE_CAP_FPS = 30

    /** The FPS cap for a [PowerManager] thermal status, or 0 for no cap. */
    fun capFor(thermalStatus: Int): Int = when {
        thermalStatus >= PowerManager.THERMAL_STATUS_SEVERE -> SEVERE_CAP_FPS
        thermalStatus == PowerManager.THERMAL_STATUS_MODERATE -> MODERATE_CAP_FPS
        else -> 0
    }

    /** [userLimit] is the player's FPS limit, with 0 meaning unlimited. Returns the limit to apply, 0 meaning unlimited. */
    fun limit(userLimit: Int, thermalStatus: Int): Int {
        val cap = capFor(thermalStatus)
        return when {
            cap == 0 -> userLimit
            userLimit == 0 -> cap
            else -> minOf(userLimit, cap)
        }
    }
}
