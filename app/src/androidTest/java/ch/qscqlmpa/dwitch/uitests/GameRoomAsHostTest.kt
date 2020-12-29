 package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseHostTest
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertCanPassTurn
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertCanPickACard
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertCardInHand
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertCardOnTable
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertGameRoomIsDisplayed
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.chooseCardForExchange
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.passTurn
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.pickACard
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.playCard
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.clickOnButton
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.PlayerHostTest
import ch.qscqlmpa.dwitchengine.ProdDwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import org.assertj.core.api.Assertions.assertThat
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

        assertCardInHand(0, Card.Hearts5)
        assertCardInHand(1, Card.Clubs3)
        assertCardOnTable(Card.Clubs2)

        playCard(0)

        val gameStateUpdatedMessage = waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedMessage.gameState.cardsOnTable.size).isEqualTo(2)

        assertCardInHand(0, Card.Clubs3)
        assertCardOnTable(Card.Hearts5)

        assertCanPickACard(false)
        assertCanPassTurn(false)
    }

    @Test
    fun pickACardAndPassTurn() {
        launch()

        goToGameRoom()

        assertCardInHand(0, Card.Hearts5)
        assertCardInHand(1, Card.Clubs3)
        assertCardOnTable(Card.Clubs2)

        assertCanPickACard(true)
        assertCanPassTurn(false)

        pickACard()

        val gameStateUpdatedMessage1 = waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedMessage1.gameState.player(host.dwitchId).hasPickedACard).isTrue

        assertCardInHand(0, Card.Hearts5)
        assertCardInHand(1, Card.Clubs3)
        assertCardInHand(2, Card.Clubs4)
        assertCardOnTable(Card.Clubs2)

        assertCanPickACard(false)
        assertCanPassTurn(true)

        passTurn()

        val gameStateUpdatedMessage2 =  waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedMessage2.gameState.player(host.dwitchId).status).isEqualTo(PlayerStatus.Waiting)

        assertCanPickACard(false)
        assertCanPassTurn(false)
    }

    @Test
    fun playAWholeRound() {
        launch()

        goToGameRoom()

        UiUtil.assertControlTextContent(R.id.gameInfoTv, R.string.round_is_beginning)

        assertCardInHand(0, Card.Hearts5)
        assertCardInHand(1, Card.Clubs3)
        assertCardOnTable(Card.Clubs2)

        playCard(1) // Local player plays Clubs3
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        assertCardInHand(0, Card.Hearts5)
        assertCardOnTable(Card.Clubs3)

        otherPlayerPlaysCard(PlayerHostTest.Guest1, Card.Spades4)
        dudeWaitASec()
        assertCardOnTable(Card.Spades4)

        playCard(0) // Local player plays Hearts5 and is done
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
        assertCardOnTable(Card.Hearts5)

        UiUtil.assertControlTextContent(R.id.gameInfoTv, R.string.round_is_over)

        clickOnButton(R.id.endGameBtn)

        dudeWaitASec()

        assertCurrentScreenIsHomeSreen()
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

        assertCardInHand(0, Card.Hearts3)
        assertCardOnTable(Card.Clubs2)

        playCard(0) // Local player plays Hearts3
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        UiUtil.assertControlTextContent(R.id.gameInfoTv, R.string.round_is_over)

        initializeNewRoundCardDealer(mapOf(
            0 to listOf(Card.Hearts5, Card.Clubs3, Card.Spades6, Card.HeartsAce), // Guest
            1 to listOf(Card.Spades6, Card.Spades4, Card.Diamonds4, Card.Clubs10) // Host
        ))

        clickOnButton(R.id.startNewRoundBtn)

        val messages = waitForNextNMessageSentByHost(2)
        messages[0] as Message.GameStateUpdatedMessage
        messages[1] as Message.CardExchangeMessage

        // The host (president) chooses the cards it wants to give up for the exchange with the asshole (guest)
        chooseCardForExchange(0)
        chooseCardForExchange(0)
        clickOnButton(R.id.exchangeBtn)

        dudeWaitASec(4)

        assertGameRoomIsDisplayed()

        assertCardInHand(0, Card.Diamonds4)
        assertCardInHand(1, Card.Clubs10)

        otherPlayerSendsCardExchangeMessage(setOf(Card.Spades6, Card.HeartsAce))

        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        assertCardInHand(0, Card.Diamonds4)
        assertCardInHand(1, Card.Clubs10)
        assertCardInHand(2, Card.Spades6)
        assertCardInHand(3, Card.HeartsAce)

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

        onView(withId(R.id.launchGameBtn))
                .check(matches(withText(R.string.wrhf_launch_game_tv)))
                .check(matches(isEnabled()))

        initializeInitialGameSetup(cardsForPlayer, rankForPlayer)

        clickOnButton(R.id.launchGameBtn)

        dudeWaitASec()

        assertGameRoomIsDisplayed()
    }
}
