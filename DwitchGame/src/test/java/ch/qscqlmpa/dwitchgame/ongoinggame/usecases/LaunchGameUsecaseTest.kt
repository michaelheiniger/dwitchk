package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.BaseUnitTest

internal class LaunchGameUsecaseTest : BaseUnitTest() {

    //TODO: Create instrumented tests that test the whole usecase without mocking all dependencies
//    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)
//
//    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)
//
//    private val mockInitialGameSetupFactory = mockk<InitialGameSetupFactory>(relaxed = true)
//
//    private lateinit var launchGameUsecase: LaunchGameUsecase
//
//    @BeforeEach
//    override fun setup() {
//        super.setup()
//        launchGameUsecase = LaunchGameUsecase(
//                mockInGameStore,
//                mockCommunicator,
//                mockAppEventRepository,
//                mockInitialGameSetupFactory
//        )
//
//        every { mockCommunicator.sendMessage(ofType(EnvelopeToSend::class)) } returns Completable.complete()
//        every { mockInitialGameSetupFactory.getInitialGameSetup(any()) } returns RandomInitialGameSetup(2)
//
//        val hostPlayer = TestEntityFactory.createHostPlayer()
//        val guest1Player = TestEntityFactory.createGuestPlayer1()
//        every { mockInGameStore.getPlayersInWaitingRoom() } returns listOf(hostPlayer, guest1Player)
//        every { mockInGameStore.getLocalPlayerDwitchId() } returns hostPlayer.dwitchId
//    }
//
//    @Test
//    fun `Send GameLaunched message`() {
//        launchTest()
//
//        val messageWrapperCap = CapturingSlot<EnvelopeToSend>()
//        verify { mockCommunicator.sendMessage(capture(messageWrapperCap)) }
//
//        val messageSent = messageWrapperCap.captured.message as Message.LaunchGameMessage
//        assertThat(messageSent.gameState).isNotNull
//    }
//
//    @Test
//    fun `Save initialized GameState in store`() {
//        launchTest()
//
//        val gameStateCap = CapturingSlot<GameState>()
//        verify { mockInGameStore.updateGameState(capture(gameStateCap)) }
//
//        assertThat(gameStateCap.captured).isNotNull
//    }
//
//    @Test
//    fun `Change room to GameRoom in service`() {
//        launchTest()
//
//        verify { mockAppEventRepository.notify(AppEvent.GameRoomJoinedByHost) }
//    }
//
//    @Test
//    fun `Update current room to GameRoom in store`() {
//        launchTest()
//
//        verify { mockInGameStore.updateGameRoom(RoomType.GAME_ROOM) }
//    }
//
//    private fun launchTest() {
//        launchGameUsecase.launchGame().test().assertComplete()
//    }

}