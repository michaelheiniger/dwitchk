package ch.qscqlmpa.dwitch.ui.ongoinggame

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class OngoingGameScreenBindingModule {

    @ContributesAndroidInjector
    abstract fun contributeWaitingRoomActivity(): WaitingRoomActivity

    @ContributesAndroidInjector
    abstract fun contributeGameRoomActivity(): GameRoomActivity
}
