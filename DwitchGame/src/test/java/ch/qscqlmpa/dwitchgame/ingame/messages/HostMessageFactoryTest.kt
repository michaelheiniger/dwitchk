package ch.qscqlmpa.dwitchgame.ingame.messages

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.model.Recipient
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class HostMessageFactoryTest : BaseUnitTest() {

    private lateinit var hostMessageFactory: HostMessageFactory

    @BeforeEach
    fun setup() {
        hostMessageFactory = HostMessageFactory(mockInGameStore)
    }

    @Test
    fun createWaitingRoomUpdateMessage() {
        val players = listOf(
            TestEntityFactory.createHostPlayer(),
            TestEntityFactory.createGuestPlayer1(
                connected = false,
                ready = false
            )
        )
        every { mockInGameStore.getPlayersInWaitingRoom() } returns players

        val envelope = hostMessageFactory.createWaitingRoomStateUpdateMessage()
        val message = (envelope.message as Message.WaitingRoomStateUpdateMessage)

        assertThat(envelope.recipient).isEqualTo(Recipient.All)
        assertThat(message.playerList[0].dwitchId).isEqualTo(DwitchPlayerId(100))
        assertThat(message.playerList[0].name).isEqualTo("Aragorn")
        assertThat(message.playerList[0].playerRole).isEqualTo(PlayerRole.HOST)
        assertThat(message.playerList[0].connected).isTrue
        assertThat(message.playerList[0].ready).isTrue

        assertThat(message.playerList[1].dwitchId).isEqualTo(DwitchPlayerId(101))
        assertThat(message.playerList[1].name).isEqualTo("Boromir")
        assertThat(message.playerList[1].playerRole).isEqualTo(PlayerRole.GUEST)
        assertThat(message.playerList[1].connected).isFalse
        assertThat(message.playerList[1].ready).isFalse
    }

    @Test
    fun createJoinAckMessage() {
        val gameCommonId = GameCommonId(UUID.randomUUID())
        val localConnectionId = ConnectionId(3)
        val playerDwitchId = DwitchPlayerId(2)

        every { mockInGameStore.getGameCommonId() } returns gameCommonId

        val envelope = hostMessageFactory.createJoinAckMessage(localConnectionId, playerDwitchId)
        val message = (envelope.message as Message.JoinGameAckMessage)

        assertThat(envelope.recipient).isEqualTo(Recipient.Single(localConnectionId))
        assertThat(message.gameCommonId).isEqualTo(gameCommonId)
        assertThat(message.playerId).isEqualTo(playerDwitchId)
    }
}
