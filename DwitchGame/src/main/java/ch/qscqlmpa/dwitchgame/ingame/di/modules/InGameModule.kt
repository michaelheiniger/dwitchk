package ch.qscqlmpa.dwitchgame.ingame.di.modules

//@Suppress("unused")
//@Module
//class InGameModule(
//    private val gameLocalId: Long,
//    private val localPlayerLocalId: Long,
//    private val hostPort: Int,
//    private val hostIpAddress: String,
//    private val inGameStore: InGameStore,
//    private val commComponent: CommunicationComponent
//) {
//
//    @OngoingGameScope
//    @Provides
//    fun provideCommServer(): CommServer {
//        return commComponent.commServer
//    }
//
//    @OngoingGameScope
//    @Provides
//    fun provideCommClient(): CommClient {
//        return commComponent.commClient
//    }
//
//    @OngoingGameScope
//    @Provides
//    fun provideConnectionStore(): ConnectionStore {
//        return commComponent.connectionStore
//    }
//
//    @OngoingGameScope
//    @Provides
//    fun provideInGameStore(): InGameStore {
//        return inGameStore
//    }
//
//    @OngoingGameScope
//    @Provides
//    fun providePlayerRole(): PlayerRole {
//        return playerRole
//    }
//
//    @Named(GAME_LOCAL_ID)
//    @OngoingGameScope
//    @Provides
//    fun provideGameLocalId(): Long {
//        return gameLocalId
//    }
//
//    @Named(LOCAL_PLAYER_LOCAL_ID)
//    @OngoingGameScope
//    @Provides
//    fun provideLocalPlayerLocalId(): Long {
//        return localPlayerLocalId
//    }
//
//    @Named(HOST_PORT)
//    @OngoingGameScope
//    @Provides
//    fun provideHostPort(): Int {
//        return hostPort
//    }
//
//    @Named(HOST_IP_ADDRESS)
//    @OngoingGameScope
//    @Provides
//    fun provideHostIpAddress(): String {
//        return hostIpAddress
//    }
//
//    @Provides
//    internal fun provideGameCommunicator(
//        playerRole: PlayerRole,
//        hostCommunicator: HostCommunicator,
//        guestCommunicator: GuestCommunicator
//    ): GameCommunicator {
//        return when (playerRole) {
//            PlayerRole.GUEST -> guestCommunicator
//            PlayerRole.HOST -> hostCommunicator
//        }
//    }
//
//    @Provides
//    internal fun provideCommunicationStateRepository(
//        playerRole: PlayerRole,
//        guestCommunicationStateRepository: GuestCommunicationStateRepository,
//        hostCommunicationStateRepository: HostCommunicationStateRepository
//    ): CommunicationStateRepository {
//        return when (playerRole) {
//            PlayerRole.GUEST -> guestCommunicationStateRepository
//            PlayerRole.HOST -> hostCommunicationStateRepository
//        }
//    }
//}
