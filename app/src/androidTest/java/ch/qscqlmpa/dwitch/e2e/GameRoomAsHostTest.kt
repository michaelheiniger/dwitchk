package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import ch.qscqlmpa.dwitch.clickOnDialogConfirmButton
import ch.qscqlmpa.dwitch.e2e.base.BaseHostTest
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardExchangeControlIsEnabled
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardExchangeControlIsHidden
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardOnTable
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardsInHand
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertGameRoomIsDisplayed
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertPlayerCanPassTurn
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertPlayerCannotPassTurn
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.chooseCardsForExchange
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.confirmCardExchange
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.endGame
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.passTurn
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.playCards
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.startNewRound
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.assertLaunchGameControlIsEnabled
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.launchGame
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.PlayerHostTest
import ch.qscqlmpa.dwitchengine.ProdDwitchFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.MessageFactory
import org.junit.Test

class GameRoomAsHostTest : BaseHostTest() {

    private var rankForPlayer: Map<DwitchPlayerId, DwitchRank> = mapOf(
        DwitchPlayerId(1) to DwitchRank.Asshole, // Host
        DwitchPlayerId(2) to DwitchRank.President // Guest
    )
    private var cardsForPlayer: Map<DwitchPlayerId, Set<Card>> = mapOf(
        DwitchPlayerId(1) to setOf(Card.Hearts5, Card.Clubs5, Card.Clubs3),
        DwitchPlayerId(2) to setOf(Card.Spades6, Card.Spades4)
    )

    @Test
    fun goToGameRoomScreen() {
        goToGameRoom()
    }

    @Test
    fun localPlayerPlaysTwoCards() {
        goToGameRoom()

        testRule.assertCardsInHand(Card.Hearts5, Card.Clubs5, Card.Clubs3)
        testRule.assertCardOnTable(Card.Blank)

        testRule.playCards(Card.Hearts5, Card.Clubs5)

        assertGameStateUpdatedMessageSent()

        testRule.assertCardsInHand(Card.Clubs3)
        testRule.assertCardOnTable(Card.Hearts5, Card.Clubs5)
    }

    @Test
    fun localPlayerPasses() {
        goToGameRoom()

        testRule.assertCardsInHand(Card.Hearts5, Card.Clubs5, Card.Clubs3)
        testRule.assertCardOnTable(Card.Blank)

        testRule.assertPlayerCanPassTurn()

        testRule.passTurn()
        assertGameStateUpdatedMessageSent()

        testRule.assertPlayerCannotPassTurn()
    }

    @Test
    fun playAWholeRoundWithHumanGuest() {
        cardsForPlayer = mapOf(
            DwitchPlayerId(1) to setOf(Card.Hearts5, Card.Clubs3),
            DwitchPlayerId(2) to setOf(Card.Spades6, Card.Spades4)
        )

        goToGameRoom()

        testRule.assertCardsInHand(Card.Hearts5, Card.Clubs3)
        testRule.assertCardOnTable(Card.Blank)

        testRule.playCards(Card.Clubs3)
        assertGameStateUpdatedMessageSent()

        testRule.assertCardsInHand(Card.Hearts5)
        testRule.assertCardOnTable(Card.Clubs3)

        otherPlayerPlaysCard(PlayerHostTest.Guest1, Card.Spades4)

        testRule.assertCardOnTable(Card.Spades4)

        testRule.playCards(Card.Hearts5) // Local player plays its last card
        assertGameStateUpdatedMessageSent()

        //TODO: Replace with PlayerHostTest or something like this
//        testRule.assertEndOfRoundResult(PlayerGuestTest.Host.info.name, getString(R.string.president_long))
//        testRule.assertEndOfRoundResult(PlayerGuestTest.LocalGuest.info.name, getString(R.string.asshole_long))

        testRule.endGame()
        testRule.clickOnDialogConfirmButton()

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun localPlayerPerformsCardExchange() {
        cardsForPlayer = mapOf(
            DwitchPlayerId(1) to setOf(Card.Hearts3), // Host
            DwitchPlayerId(2) to setOf(Card.Spades6) // Guest
        )

        goToGameRoom()

        testRule.assertCardsInHand(Card.Hearts3)
        testRule.assertCardOnTable(Card.Blank)

        testRule.playCards(Card.Hearts3)
        assertGameStateUpdatedMessageSent()

        initializeNewRoundCardDealer(
            mapOf(
                DwitchPlayerId(1) to setOf(Card.Spades3, Card.Spades4, Card.Diamonds4, Card.Clubs10), // Host
                DwitchPlayerId(2) to setOf(Card.Hearts5, Card.Clubs3, Card.Spades6, Card.HeartsAce) // Guest
            )
        )
        testRule.startNewRound()
        incrementGameIdlingResource("Click to start new round")
        assertGameStateUpdatedMessageSent()

        otherPlayerSendsCardExchangeMessage(setOf(Card.Spades6, Card.HeartsAce))
        assertGameStateUpdatedMessageSent()

        testRule.assertCardExchangeControlIsHidden()
        testRule.chooseCardsForExchange(Card.Spades3, Card.Spades4)
        testRule.assertCardExchangeControlIsEnabled()
        testRule.confirmCardExchange()

        testRule.assertGameRoomIsDisplayed()
        testRule.assertCardsInHand(Card.Diamonds4, Card.Clubs10, Card.Spades6, Card.HeartsAce)
    }

    @Test
    fun currentPlayerGetsDisconnected() {
        rankForPlayer = mapOf(
            DwitchPlayerId(1) to DwitchRank.President, // Host
            DwitchPlayerId(2) to DwitchRank.Asshole // Guest starts to play
        )

        goToGameRoom()

        incrementGameIdlingResource("Guest disconnects")
        serverTestStub.clientDisconnectsFromServer(PlayerHostTest.Guest1)

        testRule.onNodeWithTag(UiTags.waitingDialogAbortBtn).assertIsDisplayed()
    }

    private fun otherPlayerSendsCardExchangeMessage(cards: Set<Card>) {
        val message = MessageFactory.createCardsForExchangeChosenMessage(guest1.dwitchId, cards)
        serverTestStub.clientSendsMessageToServer(PlayerHostTest.Guest1, message)
    }

    private fun otherPlayerPlaysCard(guest: PlayerHostTest, card: Card) {
        incrementGameIdlingResource("Guest plays card ($guest, $card)")
        val currentGameState = inGameStore.getGameState()
        val newGameState = ProdDwitchFactory().createDwitchEngine(currentGameState).playCards(PlayedCards(card))
        serverTestStub.clientSendsMessageToServer(guest, MessageFactory.createGameStateUpdatedMessage(newGameState))
        assertGameStateUpdatedMessageSent() // Broadcasted by host to other players
    }

    private fun goToGameRoom() {
        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)
        guestBecomesReady(PlayerHostTest.Guest1)

        testRule.assertLaunchGameControlIsEnabled()

        initializeInitialGameSetup(cardsForPlayer, rankForPlayer)

        testRule.launchGame()
        waitForNextMessageSentByHost() as Message.LaunchGameMessage

        testRule.assertGameRoomIsDisplayed()
    }

    private fun assertGameStateUpdatedMessageSent() {
        waitForNextMessageSentByHost() as Message.GameStateUpdatedMessage
    }
}
