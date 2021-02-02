package ch.qscqlmpa.dwitch.uitests

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseHostTest
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.PlayerHostTest
import ch.qscqlmpa.dwitchengine.ProdDwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.Test

class GameRoomAsHostTest : BaseHostTest() {

    private var rankForPlayer: Map<Int, Rank> = mapOf(
        0 to Rank.Asshole,
        1 to Rank.President
    )
    private var cardsForPlayer: Map<Int, List<Card>> = mapOf(
        0 to listOf(Card.Hearts5, Card.Clubs3),
        1 to listOf(Card.Spades6, Card.Spades4)
    )

    @Test
    fun goToGameRoomScreen() {
        launch()

        goToGameRoom()
    }

    @Test
    fun playACard() {
        launch()

        goToGameRoom()

        GameRoomUiUtil.assertCardInHand(0, Card.Hearts5)
        GameRoomUiUtil.assertCardInHand(1, Card.Clubs3)
        GameRoomUiUtil.assertCardOnTable(Card.Clubs2)

        GameRoomUiUtil.playCard(0)

        assertGameStateUpdatedMessageSent()

        GameRoomUiUtil.assertCardInHand(0, Card.Clubs3)
        GameRoomUiUtil.assertCardOnTable(Card.Hearts5)

        GameRoomUiUtil.assertCanPickACard(false)
        GameRoomUiUtil.assertCanPassTurn(false)
    }

    @Test
    fun pickACardAndPassTurn() {
        launch()

        goToGameRoom()

        GameRoomUiUtil.assertCardInHand(0, Card.Hearts5)
        GameRoomUiUtil.assertCardInHand(1, Card.Clubs3)
        GameRoomUiUtil.assertCardOnTable(Card.Clubs2)

        GameRoomUiUtil.assertCanPickACard(true)
        GameRoomUiUtil.assertCanPassTurn(false)

        GameRoomUiUtil.pickACard()
        assertGameStateUpdatedMessageSent()

        GameRoomUiUtil.assertCardInHand(0, Card.Hearts5)
        GameRoomUiUtil.assertCardInHand(1, Card.Clubs3)
        GameRoomUiUtil.assertCardInHand(2, Card.Clubs4)
        GameRoomUiUtil.assertCardOnTable(Card.Clubs2)

        GameRoomUiUtil.assertCanPickACard(false)
        GameRoomUiUtil.assertCanPassTurn(true)

        GameRoomUiUtil.passTurn()
        assertGameStateUpdatedMessageSent()

        GameRoomUiUtil.assertCanPickACard(false)
        GameRoomUiUtil.assertCanPassTurn(false)
    }

    @Test
    fun playAWholeRound() {
        launch()

        goToGameRoom()

        UiUtil.assertControlTextContent(R.id.gameInfoTv, R.string.round_is_beginning)

        GameRoomUiUtil.assertCardInHand(0, Card.Hearts5)
        GameRoomUiUtil.assertCardInHand(1, Card.Clubs3)
        GameRoomUiUtil.assertCardOnTable(Card.Clubs2)

        GameRoomUiUtil.playCard(1) // Local player plays Clubs3
        assertGameStateUpdatedMessageSent()

        GameRoomUiUtil.assertCardInHand(0, Card.Hearts5)
        GameRoomUiUtil.assertCardOnTable(Card.Clubs3)

        otherPlayerPlaysCard(PlayerHostTest.Guest1, Card.Spades4)
        dudeWaitASec()
        GameRoomUiUtil.assertCardOnTable(Card.Spades4)

        GameRoomUiUtil.playCard(0) // Local player plays Hearts5 and is done
        assertGameStateUpdatedMessageSent()

        closeEndOfRoundDialog()

        GameRoomUiUtil.assertCardOnTable(Card.Hearts5)

        UiUtil.clickOnButton(R.id.endGameBtn)

        dudeWaitASec()

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun showEndOfRoundResults() {
        launch()

        cardsForPlayer = mapOf(
            0 to listOf(Card.Hearts3), // Host
            1 to listOf(Card.Spades6) // Guest
        )

        goToGameRoom()

        UiUtil.assertControlTextContent(R.id.gameInfoTv, R.string.round_is_beginning)

        GameRoomUiUtil.assertCardInHand(0, Card.Hearts3)
        GameRoomUiUtil.assertCardOnTable(Card.Clubs2)

        GameRoomUiUtil.playCard(0) // Local player plays Hearts3
        assertGameStateUpdatedMessageSent()

        UiUtil.assertControlTextContent(R.id.mainTextTv, Matchers.containsString("${hostName}: President"))
        UiUtil.assertControlTextContent(R.id.mainTextTv, Matchers.containsString("Boromir: Asshole"))

        closeEndOfRoundDialog()

        dudeWaitASec()

        GameRoomUiUtil.assertGameRoomIsDisplayed()
    }

    @Test
    fun playAWholeRoundAndPerformCardExchange() {
        launch()

        cardsForPlayer = mapOf(
            0 to listOf(Card.Hearts3), // Host
            1 to listOf(Card.Spades6) // Guest
        )

        goToGameRoom()

        UiUtil.assertControlTextContent(R.id.gameInfoTv, R.string.round_is_beginning)

        GameRoomUiUtil.assertCardInHand(0, Card.Hearts3)
        GameRoomUiUtil.assertCardOnTable(Card.Clubs2)

        GameRoomUiUtil.playCard(0) // Local player plays Hearts3
        assertGameStateUpdatedMessageSent()

        closeEndOfRoundDialog()

        initializeNewRoundCardDealer(
            mapOf(
                0 to listOf(Card.Hearts5, Card.Clubs3, Card.Spades6, Card.HeartsAce), // Guest
                1 to listOf(Card.Spades6, Card.Spades4, Card.Diamonds4, Card.Clubs10) // Host
            )
        )

        UiUtil.clickOnButton(R.id.startNewRoundBtn)

        // Order is not deterministic because one is sent to a specific guest and the other is broadcasted
        val messagesSent = waitForNextNMessageSentByHost(2)
        assertThat(messagesSent).anyMatch { m -> m is Message.GameStateUpdatedMessage }
        assertThat(messagesSent).anyMatch { m -> m is Message.CardExchangeMessage }
        assertThat(messagesSent.size).isEqualTo(2)

        UiUtil.assertControlEnabled(R.id.exchangeBtn, enabled = false)

        // The host (president) chooses the cards it wants to give up for the exchange with the asshole (guest)
        GameRoomUiUtil.chooseCardForExchange(0)
        GameRoomUiUtil.chooseCardForExchange(0)
        UiUtil.assertControlEnabled(R.id.exchangeBtn, enabled = true)

        UiUtil.clickOnButton(R.id.exchangeBtn)

        dudeWaitASec()

        GameRoomUiUtil.assertGameRoomIsDisplayed()

        GameRoomUiUtil.assertCardInHand(0, Card.Diamonds4)
        GameRoomUiUtil.assertCardInHand(1, Card.Clubs10)

        otherPlayerSendsCardExchangeMessage(setOf(Card.Spades6, Card.HeartsAce))

        assertGameStateUpdatedMessageSent()

        GameRoomUiUtil.assertCardInHand(0, Card.Diamonds4)
        GameRoomUiUtil.assertCardInHand(1, Card.Clubs10)
        GameRoomUiUtil.assertCardInHand(2, Card.Spades6)
        GameRoomUiUtil.assertCardInHand(3, Card.HeartsAce)

        UiUtil.assertControlTextContent(R.id.gameInfoTv, R.string.round_is_beginning)
    }

    private fun otherPlayerSendsCardExchangeMessage(cards: Set<Card>) {
        val message = MessageFactory.createCardsForExchangeChosenMessage(guest1.dwitchId, cards)
        serverTestStub.guestSendsMessageToServer(PlayerHostTest.Guest1, message, true)
    }

    private fun otherPlayerPlaysCard(guest: PlayerHostTest, card: Card) {
        val currentGameState = inGameStore.getGameState()
        val newGameState = ProdDwitchEngineFactory().create(currentGameState).playCard(card)
        serverTestStub.guestSendsMessageToServer(guest, MessageFactory.createGameStateUpdatedMessage(newGameState), true)
    }

    private fun goToGameRoom() {
        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)
        guestBecomesReady(PlayerHostTest.Guest1)

        UiUtil.assertControlEnabled(R.id.launchGameBtn, enabled = true)

        initializeInitialGameSetup(cardsForPlayer, rankForPlayer)

        UiUtil.clickOnButton(R.id.launchGameBtn)
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.LaunchGameMessage::class.java)

        dudeWaitASec()

        GameRoomUiUtil.assertGameRoomIsDisplayed()
    }

    private fun assertGameStateUpdatedMessageSent() {
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.GameStateUpdatedMessage::class.java)
    }
}
