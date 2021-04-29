package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.e2e.base.BaseGuestTest
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardExchangeIsOnGoing
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardOnTable
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertCardsInHand
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertEndOfRoundResult
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertGameRoomIsDisplayed
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.assertPlayerCannotPassTurn
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.chooseCardForExchange
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.closeGameOverDialog
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.confirmCardExchange
import ch.qscqlmpa.dwitch.e2e.utils.GameRoomUiUtil.playCard
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.utils.TestEntityFactory
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.DwitchEngine.Companion.createNewGame
import ch.qscqlmpa.dwitchengine.ProdDwitchFactory
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import org.junit.Test

class GameRoomAsGuestTest : BaseGuestTest() {

    private var rankForPlayer: Map<DwitchPlayerId, DwitchRank> = mapOf(
        PlayerGuestTest.Host.id to DwitchRank.President,
        PlayerGuestTest.LocalGuest.id to DwitchRank.Asshole
    )
    private var cardsForPlayer: Map<DwitchPlayerId, Set<Card>> = mapOf(
        PlayerGuestTest.Host.id to setOf(Card.Hearts5, Card.Clubs3),
        PlayerGuestTest.LocalGuest.id to setOf(Card.Spades6, Card.Spades4)
    )

    @Test
    fun goToGameRoomScreen() {
        goToGameRoom()
    }

    @Test
    fun localPlayerPlaysACard() {
        goToGameRoom()

        testRule.assertCardsInHand(Card.Spades4, Card.Spades6)
        testRule.assertCardOnTable(Card.Blank)

        testRule.playCard(Card.Spades4)

        waitForNextMessageSentByLocalGuest() as Message.GameStateUpdatedMessage

        testRule.assertCardsInHand(Card.Spades6)
        testRule.assertCardOnTable(Card.Spades4)
        testRule.assertPlayerCannotPassTurn()
    }

    @Test
    fun roundEnds() {
        cardsForPlayer = mapOf(
            PlayerGuestTest.Host.id to setOf(Card.Clubs3),
            PlayerGuestTest.LocalGuest.id to setOf(Card.Spades4)
        )

        goToGameRoom()

        testRule.playCard(Card.Spades4)

        waitForNextMessageSentByLocalGuest() as Message.GameStateUpdatedMessage

        testRule.assertEndOfRoundResult(PlayerGuestTest.Host.name, getString(R.string.asshole_long))
        testRule.assertEndOfRoundResult(PlayerGuestTest.LocalGuest.name, getString(R.string.president_long))
    }

    @Test
    fun localPlayerPerformCardExchange() {
        cardsForPlayer = mapOf(
            PlayerGuestTest.Host.id to setOf(Card.Hearts5),
            PlayerGuestTest.LocalGuest.id to setOf(Card.Spades6)
        )

        goToGameRoom()

        testRule.playCard(Card.Spades6)

        val gameStateUpdatedMessage = waitForNextMessageSentByLocalGuest() as Message.GameStateUpdatedMessage

        hostStartsNewRound(gameStateUpdatedMessage.gameState)
        testRule.assertCardsInHand(Card.Spades6, Card.Spades4, Card.Diamonds4, Card.Clubs10)

        testRule.chooseCardForExchange(Card.Spades4)
        testRule.chooseCardForExchange(Card.Diamonds4)
        testRule.confirmCardExchange()

        waitForNextMessageSentByLocalGuest() as Message.CardsForExchangeMessage

        testRule.assertCardExchangeIsOnGoing()
    }

    @Test
    fun hostTerminatesTheGame() {
        goToGameRoom()

        clientTestStub.serverSendsMessageToClient(Message.GameOverMessage)

        testRule.closeGameOverDialog()

        assertCurrentScreenIsHomeSreen()
    }

    private fun goToGameRoom() {
        advertiseGame()

        goToWaitingRoom()

        hostSendsJoinGameAck()
        hostSendsInitialWaitingRoomUpdate()

        testRule.onNodeWithTag(UiTags.localPlayerReadyCheckbox).performClick()

        clientTestStub.serverSendsMessageToClient(Message.LaunchGameMessage(createGameState()))

        testRule.assertGameRoomIsDisplayed()
    }

    private fun createGameState(): DwitchGameState {
        val players = listOf(
            TestEntityFactory.createHostPlayer(dwitchId = PlayerGuestTest.Host.id),
            TestEntityFactory.createGuestPlayer1(dwitchId = PlayerGuestTest.LocalGuest.id)
        )
        val initialGameSetup = DeterministicInitialGameSetup(cardsForPlayer, rankForPlayer)
        return createNewGame(players.map { p -> DwitchPlayerOnboardingInfo(p.dwitchId, p.name) }, initialGameSetup)
    }

    private fun hostStartsNewRound(gameState: DwitchGameState): DwitchGameState {
        val cardDealerFactory = DeterministicCardDealerFactory()
        cardDealerFactory.setInstance(
            DeterministicCardDealer(
                mapOf(
                    PlayerGuestTest.Host.id to setOf(Card.Hearts5, Card.Clubs3, Card.Spades6, Card.HeartsAce),
                    PlayerGuestTest.LocalGuest.id to setOf(Card.Spades6, Card.Spades4, Card.Diamonds4, Card.Clubs10)
                )
            )
        )
        val newRoundGameState = ProdDwitchFactory().createDwitchEngine(gameState).startNewRound(cardDealerFactory)
        val message = MessageFactory.createGameStateUpdatedMessage(newRoundGameState)
        clientTestStub.serverSendsMessageToClient(message)

        return newRoundGameState
    }

    private fun hostSendsInitialWaitingRoomUpdate() {
        val message = Message.WaitingRoomStateUpdateMessage(
            listOf(
                PlayerWr(
                    PlayerGuestTest.Host.id,
                    PlayerGuestTest.Host.name,
                    PlayerRole.HOST,
                    PlayerConnectionState.CONNECTED,
                    ready = true
                ),
                PlayerWr(
                    PlayerGuestTest.LocalGuest.id,
                    PlayerGuestTest.LocalGuest.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    ready = false
                )
            )
        )
        clientTestStub.serverSendsMessageToClient(message)
    }
}
