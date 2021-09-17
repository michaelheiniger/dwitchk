package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitch.ui.ingame.GuestGameViewModelBindingModule
import dagger.Subcomponent

@GameUiScope
@Subcomponent(
    modules = [
        InGameGuestUiModule::class,
        GuestGameViewModelBindingModule::class
    ]
)
interface TestInGameGuestUiComponent : InGameGuestUiComponent
