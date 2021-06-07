package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ui.ongoinggame.GameViewModelBindingModule
import dagger.Subcomponent

@GameUiScope
@Subcomponent(
    modules = [
        OnGoingGameUiModule::class,
        GameViewModelBindingModule::class
    ]
)
interface TestOngoingGameUiComponent : OngoingGameUiComponent
