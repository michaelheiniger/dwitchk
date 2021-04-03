package ch.qscqlmpa.dwitch.ui.ongoinggame

import ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange.CardExchangeActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.endofround.EndOfRoundActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OngoingGameScreenBindingModule {

    @ContributesAndroidInjector
    abstract fun contributeWaitingRoomActivity(): WaitingRoomActivity

    @ContributesAndroidInjector
    abstract fun contributeGameRoomActivity(): GameRoomActivity

    @ContributesAndroidInjector
    abstract fun contributeEndOfRoundActivity(): EndOfRoundActivity

    @ContributesAndroidInjector
    abstract fun contributeCardExchangeActivity(): CardExchangeActivity
}
