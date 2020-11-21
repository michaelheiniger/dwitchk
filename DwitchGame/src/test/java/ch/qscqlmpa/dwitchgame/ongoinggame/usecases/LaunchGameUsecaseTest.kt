package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchmodel.game.RoomType
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LaunchGameUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private val mockInitialGameSetupFactory = mockk<InitialGameSetupFactory>(relaxed = true)

    private lateinit var launchGameUsecase: LaunchGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        launchGameUsecase = LaunchGameUsecase(
                mockInGameStore,
                mockCommunicator,
                mockAppEventRepository,
                mockInitialGameSetupFactory
        )

        every { mockCommunicator.sendMessage(ofType(EnvelopeToSend::class)) } returns Completable.complete()
        every { mockInitialGameSetupFactory.getInitialGameSetup(any()) } returns RandomInitialGameSetup(2)

        val hostPlayer = TestEntityFactory.createHostPlayer()
        val guest1Player = TestEntityFactory.createGuestPlayer1()
        every { mockInGameStore.getPlayersInWaitingRoom() } returns listOf(hostPlayer, guest1Player)
        every { mockInGameStore.getLocalPlayerInGameId() } returns hostPlayer.inGameId
    }

    @Test
    fun `Send GameLaunched message`() {
        launchTest()

        val messageWrapperCap = CapturingSlot<EnvelopeToSend>()
        verify { mockCommunicator.sendMessage(capture(messageWrapperCap)) }

        val messageSent = messageWrapperCap.captured.message as Message.LaunchGameMessage
        assertThat(messageSent.gameState).isNotNull
    }

    @Test
    fun `Save initialized GameState in store`() {
        launchTest()

        val gameStateCap = CapturingSlot<GameState>()
        verify { mockInGameStore.updateGameState(capture(gameStateCap)) }

        assertThat(gameStateCap.captured).isNotNull
    }

    @Test
    fun `Change room to GameRoom in service`() {
        launchTest()

        verify { mockAppEventRepository.notify(AppEvent.GameRoomJoinedByHost) }
    }

    @Test
    fun `Update current room to GameRoom in store`() {
        launchTest()

        verify { mockInGameStore.updateGameRoom(RoomType.GAME_ROOM) }
    }

    private fun launchTest() {
        launchGameUsecase.launchGame().test().assertComplete()
    }

}