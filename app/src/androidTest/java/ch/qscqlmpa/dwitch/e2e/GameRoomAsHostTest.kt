package ch.qscqlmpa.dwitch.e2e

import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.e2e.base.BaseHostTest
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardExchangeControlIsDisabled
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardExchangeControlIsEnabled
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardOnTable
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardsInHand
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertEndOfRoundResult
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertGameRoomIsDisplayed
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertPlayerCanPassTurn
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertPlayerCannotPassTurn
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.chooseCardForExchange
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.confirmCardExchange
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.endGame
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.passTurn
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.playCard
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.startNewRound
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.assertLaunchGameControlIsEnabled
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.launchGame
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.PlayerHostTest
import ch.qscqlmpa.dwitchengine.ProdDwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameRoomAsHostTest : BaseHostTest() {

    private var rankForPlayer: Map<DwitchPlayerId, DwitchRank> = mapOf(
        DwitchPlayerId(1) to DwitchRank.Asshole, // Host
        DwitchPlayerId(2) to DwitchRank.President // Guest
    )
    private var cardsForPlayer: Map<DwitchPlayerId, Set<Card>> = mapOf(
        DwitchPlayerId(1) to setOf(Card.Hearts5, Card.Clubs3),
        DwitchPlayerId(2) to setOf(Card.Spades6, Card.Spades4)
    )

    @Test
    fun goToGameRoomScreen() {
        goToGameRoom()
    }

    @Test
    fun localPlayerPlaysACard() {
        goToGameRoom()

        testRule.assertCardsInHand(Card.Hearts5, Card.Clubs3)
        testRule.assertCardOnTable(Card.Blank)

        testRule.playCard(Card.Hearts5)

        assertGameStateUpdatedMessageSent()

        testRule.assertCardsInHand(Card.Clubs3)
        testRule.assertCardOnTable(Card.Hearts5)
    }

    @Test
    fun localPlayerPasses() {
        goToGameRoom()

        testRule.assertCardsInHand(Card.Hearts5, Card.Clubs3)
        testRule.assertCardOnTable(Card.Blank)

        testRule.assertPlayerCanPassTurn()

        testRule.passTurn()
        assertGameStateUpdatedMessageSent()

        testRule.assertPlayerCannotPassTurn()
    }

    //FIXME
    @Test
    fun playAWholeRound() {
        goToGameRoom()

        testRule.assertCardsInHand(Card.Hearts5, Card.Clubs3)
        testRule.assertCardOnTable(Card.Blank)

        testRule.playCard(Card.Clubs3)
        assertGameStateUpdatedMessageSent()

        testRule.assertCardsInHand(Card.Hearts5)
        testRule.assertCardOnTable(Card.Clubs3)

        otherPlayerPlaysCard(PlayerHostTest.Guest1, Card.Spades4)
        waitUntilPlayerDashbordIsUpdated()

        testRule.assertCardOnTable(Card.Spades4)

        testRule.playCard(Card.Hearts5) // Local player plays its last card
        assertGameStateUpdatedMessageSent()

        testRule.assertEndOfRoundResult(PlayerGuestTest.Host.name, getString(R.string.president_long))
        testRule.assertEndOfRoundResult(PlayerGuestTest.LocalGuest.name, getString(R.string.asshole_long))

        testRule.endGame()

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun roundEnds() {
        cardsForPlayer = mapOf(
            DwitchPlayerId(1) to setOf(Card.Hearts3), // Host
            DwitchPlayerId(2) to setOf(Card.Spades6) // Guest
        )

        goToGameRoom()

        testRule.assertCardsInHand(Card.Hearts3)
        testRule.assertCardOnTable(Card.Blank)

        testRule.playCard(Card.Hearts3)
        assertGameStateUpdatedMessageSent()

        testRule.assertEndOfRoundResult(hostName, getString(R.string.president_long))
        testRule.assertEndOfRoundResult(PlayerHostTest.Guest1.name, getString(R.string.asshole_long))
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

        testRule.playCard(Card.Hearts3)
        assertGameStateUpdatedMessageSent()

        initializeNewRoundCardDealer(
            mapOf(
                DwitchPlayerId(1) to setOf(Card.Spades3, Card.Spades4, Card.Diamonds4, Card.Clubs10), // Host
                DwitchPlayerId(2) to setOf(Card.Hearts5, Card.Clubs3, Card.Spades6, Card.HeartsAce) // Guest
            )
        )
        testRule.startNewRound()
        assertGameStateUpdatedMessageSent()

        otherPlayerSendsCardExchangeMessage(setOf(Card.Spades6, Card.HeartsAce))
        assertGameStateUpdatedMessageSent()

        testRule.assertCardExchangeControlIsDisabled()
        testRule.chooseCardForExchange(Card.Spades3)
        testRule.chooseCardForExchange(Card.Spades4)
        testRule.assertCardExchangeControlIsEnabled()
        testRule.confirmCardExchange()

        waitUntilPlayerDashbordIsUpdated()

        testRule.assertGameRoomIsDisplayed()
        testRule.assertCardsInHand(Card.Diamonds4, Card.Clubs10, Card.Spades6, Card.HeartsAce)
    }

    private fun otherPlayerSendsCardExchangeMessage(cards: Set<Card>) {
        val message = MessageFactory.createCardsForExchangeChosenMessage(guest1.dwitchId, cards)
        serverTestStub.guestSendsMessageToServer(PlayerHostTest.Guest1, message)
    }

    private fun otherPlayerPlaysCard(guest: PlayerHostTest, card: Card) {
        val currentGameState = inGameStore.getGameState()
        val newGameState = ProdDwitchEngineFactory().create(currentGameState).playCard(card)
        serverTestStub.guestSendsMessageToServer(guest, MessageFactory.createGameStateUpdatedMessage(newGameState))
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
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.GameStateUpdatedMessage::class.java)
    }
}
