package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import ch.qscqlmpa.dwitch.*
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.messages.MessageFactory
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class GameRoomAsHostTest : BaseHostTest() {

    private var rankForPlayer: Map<Int, Rank> = mapOf(
            0 to Rank.Asshole,
            1 to Rank.Neutral,
            2 to Rank.President
    )
    private var cardsForPlayer: Map<Int, List<Card>> = mapOf(
            0 to listOf(Card.Hearts4, Card.Clubs2),
            1 to listOf(Card.Clubs4, Card.Hearts10),
            2 to listOf(Card.Spades4, Card.Spades2)
    )

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun goToGameRoomScreen() {
        launch()

        goToGameRoom()
    }

    @Test
    fun pickACardAndPassTurn() {
        launch()

        goToGameRoom()

        assertCardInHand(0, Card.Hearts4)
        assertCardInHand(1, Card.Clubs2)
        assertCardOnTable(Card.Clubs3)

        assertCanPickACard(true)
        assertCanPassTurn(false)

        pickACard()

        val gameStateUpdatedMessage1 = waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedMessage1.gameState.localPlayer().hasPickedCard).isTrue()

        assertCardInHand(0, Card.Hearts4)
        assertCardInHand(1, Card.Clubs2)
        assertCardInHand(2, Card.Clubs5)
        assertCardOnTable(Card.Clubs3)

        assertCanPickACard(false)
        assertCanPassTurn(true)

        passTurn()

        val gameStateUpdatedMessage2 = waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedMessage2.gameState.localPlayer().state).isEqualTo(PlayerState.TurnPassed)

        assertCanPickACard(false)
        assertCanPassTurn(false)
    }

    @Test
    fun playACard() {
        launch()

        goToGameRoom()

        assertCardInHand(0, Card.Hearts4)
        assertCardInHand(1, Card.Clubs2)
        assertCardOnTable(Card.Clubs3)

        playACard(0)

        val gameStateUpdatedMessage = waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedMessage.gameState.cardsOnTable.size).isEqualTo(2)

        assertCardInHand(0, Card.Clubs2)
        assertCardOnTable(Card.Clubs3)
        assertCardOnTable(Card.Hearts4)

        assertCanPickACard(false)
        assertCanPassTurn(false)
    }

    @Ignore("Broken") //FIXME
    fun playAWholeRound() {
        launch()

        goToGameRoom()

        assertCardInHand(0, Card.Hearts4)
        assertCardInHand(1, Card.Clubs2)
        assertCardOnTable(Card.Clubs3)

        playACard(1) // Local player plays Clubs2
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        assertCardInHand(0, Card.Hearts4)
        assertCardOnTable(Card.Blank)
//        onView(withId(R.id.gameEventTv)).check(matches(withSubstring(res.getString(CardName.Two.description.id)))) //FIXME

        playACard(0) // Local player plays Hearts4 and is done
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        val gameState1 = inGameStore.getGameState()
        assertCardOnTable(Card.Hearts4)

        otherPlayerPlaysCard(Guest1, Card.Clubs4)
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        val gameState2 = inGameStore.getGameState()
        assertCardOnTable(Card.Clubs4)

        otherPlayerPlaysCard(Guest3, Card.Diamonds4)
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        val gameState3 = inGameStore.getGameState()
        assertCardOnTable(Card.Clubs4)

        otherPlayerPlaysCard(Guest2, Card.Spades2)
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        val gameState4 = inGameStore.getGameState()
        assertCardOnTable(Card.Spades2)

        otherPlayerPlaysCard(Guest2, Card.Spades4) // done
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        val gameState5 = inGameStore.getGameState()
        assertCardOnTable(Card.Spades4)

        val gameState6 = inGameStore.getGameState()
        assertCardOnTable(Card.SpadesKing)
    }

    private fun otherPlayerPlaysCard(guest: GuestIdTestHost, card: Card) {
        val player = getPlayer(guest)
        val currentGameState = inGameStore.getGameState()
        val newGameState = DwitchEngine(GameInfo(currentGameState, player.inGameId)).playCard(card).gameState
        serverTestStub.guestSendsMessageToServer(guest, MessageFactory.createGameStateUpdatedMessage(newGameState), true)
    }

    private fun playACard(position: Int) {
        onView(ViewAssertionUtil.withRecyclerView(R.id.cardsInHandRw)
                .atPositionOnView(position, R.id.cardIv))
                .perform(click())
    }

    private fun goToGameRoom() {

        goToWaitingRoom()

        guestJoinsGame(Guest1)
        guestJoinsGame(Guest2)

        guestBecomesReady(Guest1)
        guestBecomesReady(Guest2)

        onView(withId(R.id.launchGameBtn))
                .check(matches(withText(R.string.wrhf_launch_game_tv)))
                .check(matches(isEnabled()))

        initializeInitialGameSetup(cardsForPlayer, rankForPlayer)

        onView(withId(R.id.launchGameBtn))
                .perform(click())

        dudeWaitAMinute(2)

        onView(withId(R.id.cardsInHandTv)).check(matchesWithText(R.string.cards_in_hand))
    }

    private fun pickACard() {
        clickOnButton(R.id.pickBtn)
    }

    private fun passTurn() {
        clickOnButton(R.id.passBtn)
    }

    private fun assertCanPickACard(canPickACard: Boolean) {
        assertControlEnabled(R.id.pickBtn, canPickACard)
    }

    private fun assertCanPassTurn(canPassTurn: Boolean) {
        assertControlEnabled(R.id.passBtn, canPassTurn)
    }

    private fun assertCardInHand(position: Int, card: Card) {
//        onView(ViewAssertionUtil.withRecyclerView(R.id.cardsInHandRw)
//                .atPositionOnView(position, R.id.cardIv))
//                .check(matches(withContentDescription(card.resourceId.id.toString()))) //FIXME
    }

    private fun assertCardOnTable(card: Card) {
//        onView(withId(R.id.lastCardIv)).check(matches(withContentDescription(card.resourceId.id.toString()))) //FIXME
    }
}
