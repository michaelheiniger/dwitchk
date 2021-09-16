package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitch.ui.ingame.HostGameViewModelBindingModule
import dagger.Subcomponent

@GameUiScope
@Subcomponent(
    modules = [
        InGameHostUiModule::class,
        HostGameViewModelBindingModule::class
    ]
)
interface TestInGameHostUiComponent : InGameHostUiComponent
