package ch.qscqlmpa.dwitch.ui

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

    private val _command: MutableState<NavigationCommand> = mutableStateOf(NavigationCommand.Identity)
    val command: State<NavigationCommand> = _command

    fun navigateBack() {
        Logger.debug { "navigate back" }
        _command.value = NavigationCommand.Back
    }

    fun navigate(destination: Destination) {
        Logger.debug { "navigate to ${destination.routeName}" }
        _command.value = NavigationCommand.Navigate(destination)
    }
}