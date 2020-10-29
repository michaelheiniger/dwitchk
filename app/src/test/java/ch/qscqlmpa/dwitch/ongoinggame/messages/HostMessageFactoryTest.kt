package ch.qscqlmpa.dwitch.ongoinggame.messages

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class HostMessageFactoryTest : BaseUnitTest() {

    private lateinit var hostMessageFactory: HostMessageFactory

    @Before
    override fun setup() {
        super.setup()
        hostMessageFactory = HostMessageFactory(mockInGameStore)
    }

    @Test
    fun createWaitingRoomUpdateMessage() {

        val players = listOf(mockk<Player>(), mockk<Player>())
        every { mockInGameStore.getPlayersInWaitingRoom() } returns players

        val msgWrapper = hostMessageFactory.createWaitingRoomStateUpdateMessage().blockingGet()
        val message = (msgWrapper.message as Message.WaitingRoomStateUpdateMessage)

        assertThat(msgWrapper.recipient).isEqualTo(RecipientType.All)
        assertThat(message.playerList).isEqualTo(players)

        verify { mockInGameStore.getPlayersInWaitingRoom() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun createJoinAckMessage() {

        val gameCommonId = 123L
        val localConnectionId = LocalConnectionId(3)
        val playerInGameId = PlayerInGameId(2)

        val game = mockk<Game>()
        every { game.gameCommonId } returns gameCommonId
        every { mockInGameStore.getGame() } returns game

        val msgWrapper = hostMessageFactory.createJoinAckMessage(localConnectionId, playerInGameId).blockingGet()
        val message = (msgWrapper.message as Message.JoinGameAckMessage)

        assertThat(msgWrapper.recipient).isEqualTo(RecipientType.Single(localConnectionId))
        assertThat(message.gameCommonId).isEqualTo(gameCommonId)
        assertThat(message.playerInGameId).isEqualTo(playerInGameId)

        verify { mockInGameStore.getGame() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun createCancelGameMessage() {

        val msgWrapper = HostMessageFactory.createCancelGameMessage()
        val message = (msgWrapper.message as Message.CancelGameMessage)

        assertThat(msgWrapper.recipient).isEqualTo(RecipientType.All)
        assertThat(message).isEqualTo(Message.CancelGameMessage)
    }
}