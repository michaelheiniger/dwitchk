package ch.qscqlmpa.dwitch.ui.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.app.AppScope
import org.tinylog.kotlin.Logger
import javax.inject.Inject

sealed class NavigationCommand {
    object Identity : NavigationCommand()
    object Back : NavigationCommand()
    data class Navigate(val destination: Destination) : NavigationCommand()
}

@AppScope
class NavigationBridge @Inject constructor() {

    private val savedData = mutableMapOf<String, Any>()
    private val _command: MutableState<NavigationCommand> = mutableStateOf(NavigationCommand.Identity)
    val command: State<NavigationCommand> = _command

    fun navigateBack() {
        Logger.debug { "navigate back" }
        _command.value = NavigationCommand.Back
    }

    fun navigate(destination: Destination) {
        saveDataIfNeeded(destination)
        Logger.debug { "navigate to ${destination.routeName}" }
        _command.value = NavigationCommand.Navigate(destination)
    }

    fun getData(key: String): Any? {
        return savedData[key]
    }

    private fun saveDataIfNeeded(destination: Destination) {
        when (destination) {
            is HomeDestination.JoinNewGame -> saveData(destination.routeName, destination.game)
            else -> {
                // Nothing to do
            }
        }
    }

    private fun saveData(key: String, data: Any) {
        savedData[key] = data
    }
}
