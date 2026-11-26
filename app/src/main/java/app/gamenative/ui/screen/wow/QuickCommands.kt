package app.gamenative.ui.screen.wow

import android.content.Context
import org.json.JSONArray

/**
 * Chat commands the player keeps for one tap from the in-game menu, such as an addon's settings (`/fplus`), so they
 * don't have to type them on a phone or a controller. Sending one types it into WoW's chat exactly as the on-screen
 * keyboard would.
 */
object QuickCommands {
    private const val PREFS = "wow_forever"
    private const val KEY_COMMANDS = "quick_commands"
    private val DEFAULTS = listOf("/reload", "/fplus", "/fq")

    fun load(context: Context): List<String> {
        val saved = prefs(context).getString(KEY_COMMANDS, null) ?: return DEFAULTS
        return runCatching {
            val array = JSONArray(saved)
            List(array.length()) { array.getString(it) }
        }.getOrDefault(DEFAULTS)
    }

    fun save(context: Context, commands: List<String>) {
        prefs(context).edit().putString(KEY_COMMANDS, JSONArray(commands).toString()).apply()
    }

    /** Goes back to the default commands and returns them. */
    fun reset(context: Context): List<String> {
        prefs(context).edit().remove(KEY_COMMANDS).apply()
        return DEFAULTS
    }

    /** A command as typed, with the leading slash WoW needs added when it was left out. */
    fun normalize(input: String): String? {
        val command = input.trim()
        if (command.isEmpty()) return null
        return if (command.startsWith("/")) command else "/$command"
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
