package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.di.OnGoingGameQualifiers
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class InGameGuestModule(
    private val gameLocalId: Long,
    private val localPlayerLocalId: Long,
    private val hostPort: Int,
    private val hostIpAddress: String,
    private val inGameStore: InGameStore,
    private val commClient: CommClient,
    private val connectionStore: ConnectionStore
) {

    @OngoingGameScope
    @Provides
    internal fun provideInGameGuestFacade(facade: InGameGuestFacadeImpl): InGameGuestFacade {
        return facade
    }

    @OngoingGameScope
    @Provides
    fun provideCommClient(): CommClient {
        return commClient
    }

    @OngoingGameScope
    @Provides
    fun provideConnectionStore(): ConnectionStore {
        return connectionStore
    }

    @OngoingGameScope
    @Provides
    fun provideInGameStore(): InGameStore {
        return inGameStore
    }

    @OngoingGameScope
    @Provides
    fun providePlayerRole(): PlayerRole {
        return PlayerRole.GUEST
    }

    @Named(OnGoingGameQualifiers.GAME_LOCAL_ID)
    @OngoingGameScope
    @Provides
    fun provideGameLocalId(): Long {
        return gameLocalId
    }

    @Named(OnGoingGameQualifiers.LOCAL_PLAYER_LOCAL_ID)
    @OngoingGameScope
    @Provides
    fun provideLocalPlayerLocalId(): Long {
        return localPlayerLocalId
    }

    @Named(OnGoingGameQualifiers.HOST_PORT)
    @OngoingGameScope
    @Provides
    fun provideHostPort(): Int {
        return hostPort
    }

    @Named(OnGoingGameQualifiers.HOST_IP_ADDRESS)
    @OngoingGameScope
    @Provides
    fun provideHostIpAddress(): String {
        return hostIpAddress
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