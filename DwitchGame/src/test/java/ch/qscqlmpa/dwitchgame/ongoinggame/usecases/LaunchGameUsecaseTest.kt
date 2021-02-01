package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.BaseUnitTest

internal class LaunchGameUsecaseTest : BaseUnitTest() {

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
//    private fun sendGameLaunchedMessage() {
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
//    fun `Send GameLaunched message when game is a new one`() {
//        mockGameIsNew()
//        sendGameLaunchedMessage()
//    }
//
//    @Test
//    fun `Send GameLaunched message when game exists already`() {
//        mockGameExistsAlready()
//        sendGameLaunchedMessage()
//    }
//
//    @Test
//    fun `Save initialized GameState in store when game is a new one`() {
//        launchTest()
//
//        val gameStateCap = CapturingSlot<GameState>()
//        verify { mockInGameStore.updateGameState(capture(gameStateCap)) }
//
//        assertThat(gameStateCap.captured).isNotNull
//    }
//
////    private fun serviceChangesToGameRoom
//
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
//
//    private fun mockGameIsNew() {
//        every { mockInGameStore.gameIsNew() } returns true
//    }
//
//    private fun mockGameExistsAlready() {
//        every { mockInGameStore.gameIsNew() } returns false
//    }
}