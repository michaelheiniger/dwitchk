package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameScreenBindingModule
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameViewModelBindingModule
import dagger.Subcomponent

@OngoingGameUiScope
@Subcomponent(modules = [
    OnGoingGameUiModule::class,
    OngoingGameScreenBindingModule::class,
    OngoingGameViewModelBindingModule::class,
])
interface TestOngoingGameUiComponent : OngoingGameUiComponent {

}
