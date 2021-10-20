package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacadeImpl
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class InGameHostModule {

    @InGameScope
    @Binds
    internal abstract fun provideHostFacade(facade: InGameHostFacadeImpl): InGameHostFacade

    @InGameScope
    @Binds
    internal abstract fun provideGameAdvertisingFacade(facade: GameAdvertisingFacadeImpl): GameAdvertisingFacade

    @InGameScope
    @Binds
    internal abstract fun provideGameCommunicator(communication: HostCommunicator): GameCommunicator

    @InGameScope
    @Binds
    internal abstract fun provideCommunicationStateRepository(hostCommunicationStateRepository: HostCommunicationStateRepository): CommunicationStateRepository

    companion object {
        @InGameScope
        @Provides
        fun providePlayerRole(): PlayerRole {
            return PlayerRole.HOST
        }
    }
}
