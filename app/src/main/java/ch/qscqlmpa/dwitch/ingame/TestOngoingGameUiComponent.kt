package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitch.ui.ingame.GameViewModelBindingModule
import dagger.Subcomponent

@GameUiScope
@Subcomponent(
    modules = [
        OnGoingGameUiModule::class,
        GameViewModelBindingModule::class
    ]
)
interface TestOngoingGameUiComponent : OngoingGameUiComponent
