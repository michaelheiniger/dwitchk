package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameScreenBindingModule
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameViewModelBindingModule
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest.ConnectionGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.PlayerDashboardFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostFragment
import dagger.Subcomponent

@OngoingGameUiScope
@Subcomponent(modules = [
    OnGoingGameUiModule::class,
    OngoingGameScreenBindingModule::class,
    OngoingGameViewModelBindingModule::class,
])
interface OngoingGameUiComponent {

    fun inject(activity: WaitingRoomActivity)
    fun inject(fragment: WaitingRoomHostFragment)
    fun inject(fragment: WaitingRoomGuestFragment)

    fun inject(activity: GameRoomActivity)
    fun inject(fragment: GameRoomHostFragment)
    fun inject(fragment: GameRoomGuestFragment)
    fun inject(fragment: PlayerDashboardFragment)

    fun inject(fragment: ConnectionGuestFragment)
}
