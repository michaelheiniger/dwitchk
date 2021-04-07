package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameScreenBindingModule
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import dagger.Subcomponent

@OngoingGameUiScope
@Subcomponent(
    modules = [
        OnGoingGameUiModule::class,
        OngoingGameScreenBindingModule::class,
        OngoingGameViewModelBindingModule::class,
    ]
)
interface OngoingGameUiComponent {
    fun inject(activity: WaitingRoomActivity)
    fun inject(activity: GameRoomActivity)
}
