package ch.qscqlmpa.dwitch.ui.ongoinggame

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

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeWaitingRoomActivity(): WaitingRoomActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeWaitingRoomHostFragment(): WaitingRoomHostFragment

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeWaitingRoomGuestFragment(): WaitingRoomGuestFragment

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeGameRoomGuestFragment(): GameRoomGuestFragment

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeGameRoomHostFragment(): GameRoomHostFragment

    @ContributesAndroidInjector(modules = [])
    abstract fun contributePlayerDashboardFragment(): PlayerDashboardFragment
}
