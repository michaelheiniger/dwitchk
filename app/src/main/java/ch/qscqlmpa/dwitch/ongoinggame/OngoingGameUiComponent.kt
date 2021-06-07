package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ui.ongoinggame.GameFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.GameViewModelBindingModule
import dagger.Subcomponent

@GameUiScope
@Subcomponent(
    modules = [
        OnGoingGameUiModule::class,
        GameViewModelBindingModule::class
    ]
)
interface OngoingGameUiComponent {
    fun inject(fragment: GameFragment)
}
