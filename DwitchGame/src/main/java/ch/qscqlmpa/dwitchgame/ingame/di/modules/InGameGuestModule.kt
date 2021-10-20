package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class InGameGuestModule {

    @InGameScope
    @Binds
    internal abstract fun provideInGameGuestFacade(facade: InGameGuestFacadeImpl): InGameGuestFacade

    @InGameScope
    @Binds
    internal abstract fun provideGameCommunicator(guestCommunicator: GuestCommunicator): GameCommunicator

    @InGameScope
    @Binds
    internal abstract fun provideCommunicationStateRepository(guestCommunicationStateRepository: GuestCommunicationStateRepository): CommunicationStateRepository

    companion object {

        @InGameScope
        @Provides
        fun providePlayerRole(): PlayerRole {
            return PlayerRole.GUEST
        }
    }
}
