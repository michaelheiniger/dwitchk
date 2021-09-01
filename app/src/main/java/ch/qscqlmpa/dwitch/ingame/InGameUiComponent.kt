package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitch.ui.ingame.GameViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import dagger.Subcomponent

@GameUiScope
@Subcomponent(
    modules = [
        InGameUiModule::class,
        GameViewModelBindingModule::class
    ]
)
interface InGameUiComponent {
    val viewModelFactory: ViewModelFactory
}
