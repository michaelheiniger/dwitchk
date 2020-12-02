package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseGuestTest
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertCanPassTurn
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertCanPickACard
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertCardInHand
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertCardOnTable
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertGameRoomIsDisplayed
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitch.utils.TestEntityFactory
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameRoomAsGuestTest : BaseGuestTest() {

    private var rankForPlayer: Map<Int, Rank> = mapOf(
        0 to Rank.President,
        1 to Rank.Asshole
    )
    private var cardsForPlayer: Map<Int, List<Card>> = mapOf(
        0 to listOf(Card.Hearts5, Card.Clubs3),
        1 to listOf(Card.Spades6, Card.Spades4)
    )

    @Test
    fun goToGameRoomScreen() {
        launch()

        goToGameRoom()

        assertCardInHand(0, Card.Spades6)
        assertCardInHand(1, Card.Spades4)
        assertCardOnTable(Card.Clubs2)
    }

    @Test
    fun playACard() {
        launch()

        goToGameRoom()

        assertCardInHand(0, Card.Spades6)
        assertCardInHand(1, Card.Spades4)
        assertCardOnTable(Card.Clubs2)

        playACard(0)

        val gameStateUpdatedMessage = waitForNextMessageSentByLocalGuest() as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedMessage.gameState.cardsOnTable).isEqualTo(listOf(Card.Clubs2, Card.Spades6))

        assertCardInHand(0, Card.Spades4)
        assertCardOnTable(Card.Spades6)

        assertCanPickACard(false)
        assertCanPassTurn(false)
    }

    @Test
    fun gameOver() {
        launch()

        goToGameRoom()

        clientTestStub.serverSendsMessageToClient(Message.GameOverMessage, false)

        dudeWaitASec()

        UiUtil.clickOnButton(R.id.btnOk)
        UiUtil.elementIsDisplayed(R.id.gameListTv)
    }

    private fun playACard(position: Int) {
        onView(ViewAssertionUtil.withRecyclerView(R.id.cardsInHandRw).atPositionOnView(position, R.id.cardIv))
            .perform(click())
    }

    private fun goToGameRoom() {
        advertiseGame()

        goToWaitingRoom()

        clientTestStub.connectClientToServer(true)

        assertLocalGuestHasSentJoinGameMessage()

        hostSendsJoinGameAck()
        hostSendsInitialWaitingRoomUpdate()

        dudeWaitASec()

        setLocalPlayerReady()

        clientTestStub.serverSendsMessageToClient(
            Message.PlayerReadyMessage(
                PlayerGuestTest.LocalGuest.inGameId,
                true
            ), false
        )

        clientTestStub.serverSendsMessageToClient(
            Message.LaunchGameMessage(createGameState()),
            false
        )

        dudeWaitASec()

        assertGameRoomIsDisplayed()
    }

    private fun createGameState(): GameState {
        val players = listOf(
            TestEntityFactory.createHostPlayer(inGameId = PlayerGuestTest.Host.inGameId),
            TestEntityFactory.createGuestPlayer1(inGameId = PlayerGuestTest.LocalGuest.inGameId)
        )
        val initialGameSetup = DeterministicInitialGameSetup(cardsForPlayer, rankForPlayer)
        return DwitchEngine.createNewGame(players.map(Player::toPlayerInfo), initialGameSetup)
    }

    private fun hostSendsInitialWaitingRoomUpdate() {
        val gameLocalIdAtHost = 1233L
        val message = Message.WaitingRoomStateUpdateMessage(
            listOf(
                Player(
                    334,
                    PlayerGuestTest.Host.inGameId,
                    gameLocalIdAtHost,
                    PlayerGuestTest.Host.name,
                    PlayerRole.HOST,
                    PlayerConnectionState.CONNECTED,
                    true
                ),
                Player(
                    335,
                    PlayerGuestTest.LocalGuest.inGameId,
                    gameLocalIdAtHost,
                    PlayerGuestTest.LocalGuest.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    false
                ),
            )
        )
        clientTestStub.serverSendsMessageToClient(message, false)
    }
}
