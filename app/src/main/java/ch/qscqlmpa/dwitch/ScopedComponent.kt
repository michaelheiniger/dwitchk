package ch.qscqlmpa.dwitch

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * ScopedComponent extends ViewModel in order to be "storable" in a ViewModelStore.
 * We use ViewModels to store Dagger components scoped to a UI element like an Activity or a NavBackStackEntry,
 * more precisely a ViewModelStoreOwner.
 * This way, the Dagger component has the same lifecycle as the UI "level" it is tied to.
 */
abstract class ScopedComponent : ViewModel()

/**
 * Create a Dagger component that is scoped to a UI element like an Activity or a NavBackStackEntry.
 */
@Composable
inline fun <reified C : ScopedComponent> daggerUiScopedComponent(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    crossinline componentFactory: () -> C
): C = androidx.lifecycle.viewmodel.compose.viewModel(
    viewModelStoreOwner = viewModelStoreOwner,
    factory = object : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
            return componentFactory() as VM
        }
    }
)
