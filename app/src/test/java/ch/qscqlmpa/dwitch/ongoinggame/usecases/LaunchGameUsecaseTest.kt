package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetup
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.game.GameState
import io.mockk.*
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LaunchGameUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private val mockInitialGameSetupFactory = mockk<InitialGameSetupFactory>(relaxed = true)

    private lateinit var launchGameUsecase: LaunchGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        launchGameUsecase = LaunchGameUsecase(
                mockInGameStore,
                mockCommunicator,
                mockServiceManager,
                mockInitialGameSetupFactory
        )

        every { mockInGameStore.updateGameRoom(RoomType.GAME_ROOM) } just Runs
        every { mockServiceManager.goToHostGameRoom() } just Runs
        every { mockCommunicator.sendMessage(ofType(EnvelopeToSend::class)) } returns Completable.complete()
        every { mockInitialGameSetupFactory.getInitialGameSetup(any()) } returns RandomInitialGameSetup(2)

        val hostPlayer = TestEntityFactory.createHostPlayer()
        val guest1Player = TestEntityFactory.createGuestPlayer1()
        every { mockInGameStore.getPlayersInWaitingRoom() } returns listOf(hostPlayer, guest1Player)
        every { mockInGameStore.getLocalPlayerInGameId() } returns hostPlayer.inGameId
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockCommunicator, mockServiceManager, mockInitialGameSetupFactory)
    }

    @Test
    fun `Send GameLaunched message`() {

        launchGameUsecase.launchGame().test().assertComplete()

        val messageWrapperCap = CapturingSlot<EnvelopeToSend>()
        verify { mockCommunicator.sendMessage(capture(messageWrapperCap)) }

        val messageSent = messageWrapperCap.captured.message as Message.LaunchGameMessage
        assertThat(messageSent.gameState).isNotNull()
    }

    @Test
    fun `Save initialized GameState in store`() {

        launchGameUsecase.launchGame().test().assertComplete()

        val gameStateCap = CapturingSlot<GameState>()
        verify { mockInGameStore.updateGameState(capture(gameStateCap)) }

        assertThat(gameStateCap.captured).isNotNull()
    }

    @Test
    fun `Change room to GameRoom in service`() {

        launchGameUsecase.launchGame().test().assertComplete()

        verify { mockServiceManager.goToHostGameRoom() }
    }

    @Test
    fun `Update current room to GameRoom in store`() {

        launchGameUsecase.launchGame().test().assertComplete()

        verify { mockInGameStore.updateGameRoom(RoomType.GAME_ROOM) }
    }
}