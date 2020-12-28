package ch.qscqlmpa.dwitch.ui.ongoinggame

import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest.ConnectionGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host.ConnectionHostFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.PlayerDashboardFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class OngoingGameScreenBindingModule {

    @ContributesAndroidInjector
    abstract fun contributeWaitingRoomActivity(): WaitingRoomActivity

    @ContributesAndroidInjector
    abstract fun contributeWaitingRoomHostFragment(): WaitingRoomHostFragment

    @ContributesAndroidInjector
    abstract fun contributeWaitingRoomGuestFragment(): WaitingRoomGuestFragment

    @ContributesAndroidInjector
    abstract fun contributeGameRoomGuestFragment(): GameRoomGuestFragment

    @ContributesAndroidInjector
    abstract fun contributeGameRoomHostFragment(): GameRoomHostFragment

    @ContributesAndroidInjector
    abstract fun contributePlayerDashboardFragment(): PlayerDashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeConnectionGuestFragment(): ConnectionGuestFragment

    @ContributesAndroidInjector
    abstract fun contributeConnectionHostFragment(): ConnectionHostFragment
}
