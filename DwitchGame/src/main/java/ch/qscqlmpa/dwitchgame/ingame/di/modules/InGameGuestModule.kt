package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.ingame.CommClient
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchgame.ingame.di.OnGoingGameQualifiers
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class InGameGuestModule(
    private val gameLocalId: Long,
    private val localPlayerLocalId: Long,
    private val advertisedGame: GameAdvertisingInfo,
    private val inGameStore: InGameStore,
    private val commClient: CommClient,
    private val connectionStore: ConnectionStore
) {

    @InGameScope
    @Provides
    internal fun provideInGameGuestFacade(facade: InGameGuestFacadeImpl): InGameGuestFacade {
        return facade
    }

    @InGameScope
    @Provides
    fun provideCommClient(): CommClient {
        return commClient
    }

    @InGameScope
    @Provides
    fun provideConnectionStore(): ConnectionStore {
        return connectionStore
    }

    @InGameScope
    @Provides
    fun provideInGameStore(): InGameStore {
        return inGameStore
    }

    @InGameScope
    @Provides
    fun providePlayerRole(): PlayerRole {
        return PlayerRole.GUEST
    }

    @Named(OnGoingGameQualifiers.GAME_LOCAL_ID)
    @InGameScope
    @Provides
    fun provideGameLocalId(): Long {
        return gameLocalId
    }

    @Named(OnGoingGameQualifiers.LOCAL_PLAYER_LOCAL_ID)
    @InGameScope
    @Provides
    fun provideLocalPlayerLocalId(): Long {
        return localPlayerLocalId
    }

    @Named(OnGoingGameQualifiers.ADVERTISED_GAME)
    @InGameScope
    @Provides
    fun provideAdvertisedGame(): GameAdvertisingInfo {
        return advertisedGame
    }

    @Provides
    internal fun provideGameCommunicator(guestCommunicator: GuestCommunicator): GameCommunicator {
        return guestCommunicator
    }

    @Provides
    internal fun provideCommunicationStateRepository(guestCommunicationStateRepository: GuestCommunicationStateRepository): CommunicationStateRepository {
        return guestCommunicationStateRepository
    }
}
