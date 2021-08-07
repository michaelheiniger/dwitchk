package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitch.ui.ingame.GameViewModelBindingModule
import dagger.Subcomponent

@GameUiScope
@Subcomponent(
    modules = [
        InGameUiModule::class,
        GameViewModelBindingModule::class
    ]
)
interface TestInGameUiComponent : InGameUiComponent
