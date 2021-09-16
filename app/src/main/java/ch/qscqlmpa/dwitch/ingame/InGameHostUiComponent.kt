package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitch.ui.ingame.HostGameViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import dagger.Subcomponent

@GameUiScope
@Subcomponent(
    modules = [
        InGameHostUiModule::class,
        HostGameViewModelBindingModule::class
    ]
)
interface InGameHostUiComponent {
    val viewModelFactory: ViewModelFactory
}
