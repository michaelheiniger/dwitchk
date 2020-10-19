package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest

class PlayerReadyUsecaseTest : BaseUnitTest() {

//    private val gameLocalId = 1L
//
//    private lateinit var mockCommunicator: GuestCommunicator
//
//    private lateinit var usecase: PlayerReadyUsecase
//
//    @BeforeEach()
//    override fun setup() {
//        super.setup()
//
//        usecase = PlayerReadyUsecase(gameLocalId, mockStore, mockCommunicator)
//
//        every { mockCommunicator.sendMessage(ofType(MessageWrapper::class)) } returns Completable.complete()
//    }
//
//    @Test
//    fun `Player ready state is updated to true`() {
//
//        val localPlayer = TestEntityFactory.createGuestPlayer1()
//        val game = TestEntityFactory.createGameInWaitingRoom(localPlayer.id)
//
//        every { mockStore.getGame(gameLocalId) } returns game
//        every { mockStore.getPlayer(localPlayer.id) } returns localPlayer
//
//        usecase.updateReadyState(true)
//
//        verify { mockStore.g }
//
//        val messageSentRef = MessageWrapper(RecipientType.All, PlayerReadyMessage(localPlayer.inGameId, true))
//        verify { mockCommunicator.sendMessage(messageSentRef) }
//    }
//
//    @Test
//    fun `PlayerReadyMessage message is sent`() {
//
//    }
}