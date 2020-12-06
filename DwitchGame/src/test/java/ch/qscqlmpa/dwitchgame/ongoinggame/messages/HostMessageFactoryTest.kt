package ch.qscqlmpa.dwitchgame.ongoinggame.messages

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.Player
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HostMessageFactoryTest : BaseUnitTest() {

    private lateinit var hostMessageFactory: HostMessageFactory

    @BeforeEach
    override fun setup() {
        super.setup()
        hostMessageFactory = HostMessageFactory(mockInGameStore)
    }

    @Test
    fun createWaitingRoomUpdateMessage() {

        val players = listOf<Player>(mockk(), mockk())
        every { mockInGameStore.getPlayersInWaitingRoom() } returns players

        val msgWrapper = hostMessageFactory.createWaitingRoomStateUpdateMessage().blockingGet()
        val message = (msgWrapper.message as Message.WaitingRoomStateUpdateMessage)

        assertThat(msgWrapper.recipient).isEqualTo(Recipient.AllGuests)
        assertThat(message.playerList).isEqualTo(players)

        verify { mockInGameStore.getPlayersInWaitingRoom() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun createJoinAckMessage() {

        val gameCommonId = GameCommonId(123L)
        val localConnectionId = ConnectionId(3)
        val playerInGameId = PlayerInGameId(2)

        val game = mockk<Game>()
        every { game.gameCommonId } returns gameCommonId
        every { mockInGameStore.getGame() } returns game

        val msgWrapper = hostMessageFactory.createJoinAckMessage(localConnectionId, playerInGameId).blockingGet()
        val message = (msgWrapper.message as Message.JoinGameAckMessage)

        assertThat(msgWrapper.recipient).isEqualTo(Recipient.SingleGuest(localConnectionId))
        assertThat(message.gameCommonId).isEqualTo(gameCommonId)
        assertThat(message.playerInGameId).isEqualTo(playerInGameId)

        verify { mockInGameStore.getGame() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun createCancelGameMessage() {
        val msgWrapper = HostMessageFactory.createCancelGameMessage()
        val message = (msgWrapper.message as Message.CancelGameMessage)

        assertThat(msgWrapper.recipient).isEqualTo(Recipient.AllGuests)
        assertThat(message).isEqualTo(Message.CancelGameMessage)
    }
}