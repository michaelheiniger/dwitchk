package ch.qscqlmpa.dwitch.ui.ongoinggame

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.PlayerDashboardFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OngoingGameScreenBindingModule {

    @ContributesAndroidInjector
    abstract fun contributeWaitingRoomActivity(): WaitingRoomActivity

    @ContributesAndroidInjector
    abstract fun contributeGameRoomGuestFragment(): GameRoomGuestFragment

    @ContributesAndroidInjector
    abstract fun contributeGameRoomHostFragment(): GameRoomHostFragment

    @ContributesAndroidInjector
    abstract fun contributePlayerDashboardFragment(): PlayerDashboardFragment
}
