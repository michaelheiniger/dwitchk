package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import ch.qscqlmpa.dwitch.PlayerHostTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseHostTest
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertCanPassTurn
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertCanPickACard
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertCardInHand
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertCardOnTable
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.assertGameRoomIsDisplayed
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.passTurn
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUtil.pickACard
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.clickOnButton
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchcommunication.model.Message
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

//        dudeWaitASec()

        playACard(0)

//        dudeWaitASec()

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
        assertThat(gameStateUpdatedMessage1.gameState.player(host.inGameId).hasPickedCard).isTrue

        assertCardInHand(0, Card.Hearts5)
        assertCardInHand(1, Card.Clubs3)
        assertCardInHand(2, Card.Clubs4)
        assertCardOnTable(Card.Clubs2)

        assertCanPickACard(false)
        assertCanPassTurn(true)

        passTurn()

        val gameStateUpdatedMessage2 =  waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedMessage2.gameState.player(host.inGameId).state).isEqualTo(PlayerState.Waiting)

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

        playACard(1) // Local player plays Clubs3
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage

        assertCardInHand(0, Card.Hearts5)
        assertCardOnTable(Card.Clubs3)

        otherPlayerPlaysCard(PlayerHostTest.Guest1, Card.Spades4)
        dudeWaitASec()
        assertCardOnTable(Card.Spades4)

        playACard(0) // Local player plays Hearts5 and is done
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
        assertCardOnTable(Card.Hearts5)

        UiUtil.assertControlTextContent(R.id.gameInfoTv, R.string.round_is_over)

        clickOnButton(R.id.endGameBtn)

        dudeWaitASec()

        assertCurrentScreenIsHomeSreen()
    }

    private fun otherPlayerPlaysCard(guest: PlayerHostTest, card: Card) {
        val currentGameState = inGameStore.getGameState()
        val newGameState = DwitchEngine(currentGameState).playCard(card)
        serverTestStub.guestSendsMessageToServer(guest, MessageFactory.createGameStateUpdatedMessage(newGameState), true)
    }

    private fun playACard(position: Int) {
        onView(ViewAssertionUtil.withRecyclerView(R.id.cardsInHandRw)
                .atPositionOnView(position, R.id.cardIv))
                .perform(click())
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
