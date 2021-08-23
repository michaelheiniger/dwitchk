package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.clickOnDialogConfirmButton
import ch.qscqlmpa.dwitch.e2e.base.BaseGuestTest
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardExchangeIsOnGoing
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardOnTable
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardsInHand
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertEndOfRoundResult
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertGameRoomIsDisplayed
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertPlayerCannotPassTurn
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.chooseCardsForExchange
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.confirmCardExchange
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.playCards
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.utils.TestEntityFactory
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.OnStartEvent
import ch.qscqlmpa.dwitchengine.DwitchEngine.Companion.createNewGame
import ch.qscqlmpa.dwitchengine.ProdDwitchFactory
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.MessageFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameRoomAsGuestTest : BaseGuestTest() {

    private var rankForPlayer: Map<DwitchPlayerId, DwitchRank> = mapOf(
        PlayerGuestTest.Host.info.dwitchId to DwitchRank.President,
        PlayerGuestTest.LocalGuest.info.dwitchId to DwitchRank.Asshole
    )
    private var cardsForPlayer: Map<DwitchPlayerId, Set<Card>> = mapOf(
        PlayerGuestTest.Host.info.dwitchId to setOf(Card.Hearts5, Card.Clubs3),
        PlayerGuestTest.LocalGuest.info.dwitchId to setOf(Card.Spades6, Card.Spades4)
    )

    private lateinit var lastGameState: DwitchGameState

    @Test
    fun goToGameRoomScreen() {
        goToGameRoom()
    }

    @Test
    fun localPlayerDisconnectsAndReconnects() {
        goToGameRoom()

        testRule.onNodeWithTag(UiTags.passTurnControl).assertIsDisplayed()

        clientTestStub.breakConnectionWithServer()
        incrementGameIdlingResource("Connection with host broken")

        testRule.onNodeWithText(getString(R.string.disconnected_from_host))
        testRule.onNodeWithTag(UiTags.reconnect).assertIsDisplayed()

        testRule.onNodeWithTag(UiTags.reconnect).performClick()
        connectClientToServer(OnStartEvent.Success, incrementIdlingResource = true)
        waitForNextMessageSentByLocalGuest() as Message.RejoinGameMessage
        hostSendsRejoinGameAck_waitingRoom()
        hostSendsHostAndLocalGuestState()
        hostSendsLastGameState()

        testRule.onNodeWithTag(UiTags.passTurnControl).assertIsDisplayed()
    }

    @Test
    fun localPlayerPlaysACard() {
        goToGameRoom()

        testRule.assertCardsInHand(Card.Spades4, Card.Spades6)
        testRule.assertCardOnTable(Card.Blank)

        testRule.playCards(Card.Spades4)
        assertGameStateUpdatedMessageSent()

        testRule.assertCardsInHand(Card.Spades6)
        testRule.assertCardOnTable(Card.Spades4)
        testRule.assertPlayerCannotPassTurn()
    }

    @Test
    fun playAWholeRound() {
        cardsForPlayer = mapOf(
            PlayerGuestTest.Host.info.dwitchId to setOf(Card.Spades6, Card.Spades4),
            PlayerGuestTest.LocalGuest.info.dwitchId to setOf(Card.Hearts5, Card.Clubs3)
        )

        goToGameRoom()

        testRule.assertCardsInHand(Card.Hearts5, Card.Clubs3)
        testRule.assertCardOnTable(Card.Blank)

        testRule.playCards(Card.Clubs3)
        assertGameStateUpdatedMessageSent()

        testRule.assertCardsInHand(Card.Hearts5)
        testRule.assertCardOnTable(Card.Clubs3)

        hostPlaysCard(Card.Spades4)

        testRule.assertCardOnTable(Card.Spades4)

        testRule.playCards(Card.Hearts5) // Local player plays its last card
        assertGameStateUpdatedMessageSent()

        testRule.assertEndOfRoundResult(PlayerGuestTest.Host.info.name, getString(R.string.asshole_long))
        testRule.assertEndOfRoundResult(PlayerGuestTest.LocalGuest.info.name, getString(R.string.president_long))

        hostEndsGame()
        testRule.clickOnDialogConfirmButton()

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun localPlayerPerformsCardExchange() {
        cardsForPlayer = mapOf(
            PlayerGuestTest.Host.info.dwitchId to setOf(Card.Hearts5),
            PlayerGuestTest.LocalGuest.info.dwitchId to setOf(Card.Spades6)
        )

        goToGameRoom()

        testRule.playCards(Card.Spades6)

        val gameStateUpdatedMessage = assertGameStateUpdatedMessageSent()

        hostStartsNewRound(gameStateUpdatedMessage.gameState)
        testRule.assertCardsInHand(Card.Spades6, Card.Spades4, Card.Diamonds4, Card.Clubs10)

        testRule.chooseCardsForExchange(Card.Spades4, Card.Diamonds4)
        testRule.confirmCardExchange()

        waitForNextMessageSentByLocalGuest() as Message.CardsForExchangeMessage

        // Display loading screen while other players perform the card exchange
        testRule.assertCardExchangeIsOnGoing()
    }

    private fun goToGameRoom() {
        goToWaitingRoomWithHostAndLocalGuest()

        localPlayerToggleReadyCheckbox()

        lastGameState = createGameState()
        clientTestStub.serverSendsMessageToClient(Message.LaunchGameMessage(lastGameState))

        testRule.assertGameRoomIsDisplayed()
    }

    private fun hostPlaysCard(card: Card) {
        incrementGameIdlingResource("Host plays a card")
        val currentGameState = inGameStore.getGameState()
        val newGameState = ProdDwitchFactory().createDwitchEngine(currentGameState).playCards(PlayedCards(card))
        clientTestStub.serverSendsMessageToClient(MessageFactory.createGameStateUpdatedMessage(newGameState))
    }

    private fun hostEndsGame() {
        incrementGameIdlingResource("Host ends game: screen updated")
        clientTestStub.serverSendsMessageToClient(Message.GameOverMessage)
        incrementGameIdlingResource("Host ends game: communication state updated to Disconnected")
        clientTestStub.serverClosesConnectionWithClient()
    }

    private fun createGameState(): DwitchGameState {
        val players = listOf(
            TestEntityFactory.createHostPlayer(dwitchId = PlayerGuestTest.Host.info.dwitchId),
            TestEntityFactory.createGuestPlayer1(dwitchId = PlayerGuestTest.LocalGuest.info.dwitchId)
        )
        val initialGameSetup = DeterministicInitialGameSetup(cardsForPlayer, rankForPlayer)
        return createNewGame(players.map { p -> DwitchPlayerOnboardingInfo(p.dwitchId, p.name) }, initialGameSetup)
    }

    private fun assertGameStateUpdatedMessageSent(): Message.GameStateUpdatedMessage {
        val messageSent = waitForNextMessageSentByLocalGuest()
        assertThat(messageSent).isInstanceOf(Message.GameStateUpdatedMessage::class.java)
        return messageSent as Message.GameStateUpdatedMessage
    }

    private fun hostStartsNewRound(gameState: DwitchGameState): DwitchGameState {
        incrementGameIdlingResource("Host start new round")
        val cardDealerFactory = DeterministicCardDealerFactory()
        cardDealerFactory.setInstance(
            DeterministicCardDealer(
                mapOf(
                    PlayerGuestTest.Host.info.dwitchId to setOf(Card.Hearts5, Card.Clubs3, Card.Spades7, Card.HeartsAce),
                    PlayerGuestTest.LocalGuest.info.dwitchId to setOf(Card.Spades6, Card.Spades4, Card.Diamonds4, Card.Clubs10)
                )
            )
        )
        val newRoundGameState = ProdDwitchFactory().createDwitchEngine(gameState).startNewRound(cardDealerFactory)
        val message = MessageFactory.createGameStateUpdatedMessage(newRoundGameState)
        clientTestStub.serverSendsMessageToClient(message)

        return newRoundGameState
    }

    private fun hostSendsHostAndLocalGuestState() {
        val message = Message.WaitingRoomStateUpdateMessage(
            listOf(
                PlayerGuestTest.Host.info,
                PlayerGuestTest.LocalGuest.info.copy(connected = true)
            )
        )
        clientTestStub.serverSendsMessageToClient(message)
    }

    private fun hostSendsLastGameState() {
        clientTestStub.serverSendsMessageToClient(Message.GameStateUpdatedMessage(lastGameState))
    }
}
