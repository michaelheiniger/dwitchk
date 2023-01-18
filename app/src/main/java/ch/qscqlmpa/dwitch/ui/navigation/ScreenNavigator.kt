package ch.qscqlmpa.dwitch.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import ch.qscqlmpa.dwitch.ActivityScope
import org.tinylog.kotlin.Logger
import javax.inject.Inject

/**
 * Navigate between screens.
 * Activity-scoped: includes all Composables to enable providing data as argument of a destination.
 */
@ActivityScope
class ScreenNavigator @Inject constructor() {
    private var navController: NavController? = null

    private val savedData = mutableMapOf<String, Any>()

    /**
     * Provide the current navHostController. The lifecycle of the controller is shorter than of Activity (e.g. config change).
     * Hence, the new instance must be provided before navigating.
     * TODO: Find better solution.
     */
    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun clearNavController() {
        this.navController = null
    }

    fun navigateBack() {
        Logger.debug { "Order navigation back" }
        navController!!.popBackStack()
    }

    fun navigate(
        destination: Destination,
        navOptions: NavOptions? = null
    ) {
        saveDataIfNeeded(destination)
        Logger.debug { "Order navigation to ${destination.routeName}" }
        navController!!.navigate(
            route = destination.routeName,
            navOptions = navOptions
        )
    }

    fun getData(key: String): Any? {
        return savedData[key]
    }

    private fun saveDataIfNeeded(destination: Destination) {
        destination.dataToSave().forEach { (param, data) -> savedData[param] = data }
    }
}

fun navOptionsPopUpToInclusive(destination: Destination): NavOptions {
    return navOptions {
        popUpTo(destination.routeName) {
            inclusive = true
        }
    }
}
