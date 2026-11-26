package app.gamenative.ui.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import app.gamenative.ui.screen.wow.QuickCommands

/**
 * The saved chat commands: tapping one closes the menu and types it into the game's chat. Commands can be added and
 * removed here, and are kept for later sessions.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickCommandsDialog(
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
) {
    val context = LocalContext.current
    var commands by remember { mutableStateOf(QuickCommands.load(context)) }
    var newCommand by remember { mutableStateOf("") }

    fun update(list: List<String>) {
        commands = list
        QuickCommands.save(context, list)
    }

    fun add() {
        val command = QuickCommands.normalize(newCommand) ?: return
        if (command !in commands) update(commands + command)
        newCommand = ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick commands") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Tap a command to type it into the game's chat.",
                    style = MaterialTheme.typography.bodySmall,
                )
                FlowRow(
                    modifier = Modifier.heightIn(max = 160.dp).verticalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    commands.forEach { command ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalButton(onClick = { onSend(command) }) {
                                Text(command, maxLines = 1)
                            }
                            IconButton(onClick = { update(commands - command) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove $command")
                            }
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newCommand,
                        onValueChange = { newCommand = it },
                        placeholder = { Text("/command") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { add() }),
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { add() }, enabled = newCommand.isNotBlank()) {
                        Icon(Icons.Default.Add, contentDescription = "Add command")
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        dismissButton = {
            TextButton(onClick = { commands = QuickCommands.reset(context) }) {
                Text("Reset")
            }
        },
        // Wider than an AlertDialog's own width, which leaves a landscape phone a narrow column.
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.6f).widthIn(max = 640.dp),
    )
}
