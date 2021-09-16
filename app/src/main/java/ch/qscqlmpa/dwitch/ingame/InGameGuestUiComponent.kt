package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitch.ui.ingame.GuestGameViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import dagger.Subcomponent

@GameUiScope
@Subcomponent(
    modules = [
        InGameGuestUiModule::class,
        GuestGameViewModelBindingModule::class
    ]
)
interface InGameGuestUiComponent {
    val viewModelFactory: ViewModelFactory
}
