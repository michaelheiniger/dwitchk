package ch.qscqlmpa.dwitch.ui.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.navOptions
import ch.qscqlmpa.dwitch.app.AppScope
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@AppScope
class NavigationBridge @Inject constructor() {

    private val savedData = mutableMapOf<String, Any>()
    private val _command: MutableState<NavigationCommand> = mutableStateOf(NavigationCommand.Identity)
    val command: State<NavigationCommand> = _command

    fun navigateBack() {
        Logger.debug { "Order navigation back" }
        _command.value = NavigationCommand.Back
    }

    fun navigate(destination: Destination) {
        navigate(NavigationData(destination))
    }

    fun navigate(navData: NavigationData) {
        saveDataIfNeeded(navData.destination)
        Logger.debug { "Order navigation to ${navData.destination.routeName}" }
        _command.value = NavigationCommand.Navigate(navData)
    }

    fun getData(key: String): Any? {
        return savedData[key]
    }

    private fun saveDataIfNeeded(destination: Destination) {
        destination.dataToSave().forEach { (param, data) -> savedData[param] = data }
    }
}

sealed class NavigationCommand {
    object Identity : NavigationCommand()
    object Back : NavigationCommand()
    data class Navigate(val navData: NavigationData) : NavigationCommand()
}

data class NavigationData(
    val destination: Destination,
    val navOptions: NavOptions?,
    val navigatorExtras: Navigator.Extras?
) {
    constructor(destination: Destination) : this(destination, null, null)
    constructor(destination: Destination, navOptions: NavOptions) : this(destination, navOptions, null)
}

fun navOptionsPopUpToInclusive(routeName: String): NavOptions {
    return navOptions {
        popUpTo(routeName) {
            inclusive = true
        }
    }
}