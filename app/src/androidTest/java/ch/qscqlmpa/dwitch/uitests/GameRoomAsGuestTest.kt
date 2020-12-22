package ch.qscqlmpa.dwitch.uitests

import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseGuestTest
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertCanPassTurn
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertCanPickACard
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertCardInHand
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertCardOnTable
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.assertGameRoomIsDisplayed
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.chooseCardForExchange
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.confirmCardsChoiceForExchange
import ch.qscqlmpa.dwitch.uitests.utils.GameRoomUiUtil.playCard
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.clickOnButton
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.elementIsDisplayed
import ch.qscqlmpa.dwitch.utils.TestEntityFactory
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
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

        GameRoomUiUtil.playCard(0)

        val gameStateUpdatedMessage = waitForNextMessageSentByLocalGuest() as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedMessage.gameState.cardsOnTable).isEqualTo(listOf(Card.Clubs2, Card.Spades6))

        assertCardInHand(0, Card.Spades4)
        assertCardOnTable(Card.Spades6)

        assertCanPickACard(false)
        assertCanPassTurn(false)
    }

    @Test
    fun cardExchange() {
        launch()

        cardsForPlayer = mapOf(
            0 to listOf(Card.Hearts5),
            1 to listOf(Card.Spades6)
        )

        goToGameRoom()

        playCard(0)

        val gameStateUpdatedMessage = waitForNextMessageSentByLocalGuest() as Message.GameStateUpdatedMessage
        val newRoundGameState = hostStartsNewRound(gameStateUpdatedMessage.gameState)
        hostSendsCardExchangeMessage(newRoundGameState)

        dudeWaitASec()

        assertCardInHand(0, Card.Spades6)
        assertCardInHand(1, Card.Spades4)
        assertCardInHand(2, Card.Diamonds4)
        assertCardInHand(3, Card.Clubs10)

        chooseCardForExchange(1)
        chooseCardForExchange(1)

        confirmCardsChoiceForExchange()

        val cardsForExchangeMessageMessage = waitForNextMessageSentByLocalGuest() as Message.CardsForExchangeMessage
        assertThat(cardsForExchangeMessageMessage.playerId).isEqualTo(PlayerGuestTest.LocalGuest.inGameId)
        assertThat(cardsForExchangeMessageMessage.cards).isEqualTo(setOf(Card.Spades4, Card.Diamonds4))

        assertGameRoomIsDisplayed()
    }


    @Test
    fun gameOver() {
        launch()

        goToGameRoom()

        clientTestStub.serverSendsMessageToClient(Message.GameOverMessage, false)

        dudeWaitASec()

        clickOnButton(R.id.btnOk)
        elementIsDisplayed(R.id.gameListTv)
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

        clientTestStub.serverSendsMessageToClient(Message.LaunchGameMessage(createGameState()), false)

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

    private fun hostStartsNewRound(gameState: GameState): GameState {
        val cardDealerFactory = DeterministicCardDealerFactory()
        cardDealerFactory.setInstance(
            DeterministicCardDealer(
                mapOf(
                    0 to listOf(Card.Hearts5, Card.Clubs3, Card.Spades6, Card.HeartsAce),
                    1 to listOf(Card.Spades6, Card.Spades4, Card.Diamonds4, Card.Clubs10)
                )
            )
        )
        val newRoundGameState = DwitchEngine(gameState).startNewRound(cardDealerFactory)
        val message = MessageFactory.createGameStateUpdatedMessage(newRoundGameState)
        clientTestStub.serverSendsMessageToClient(message, false)

        return newRoundGameState
    }

    private fun hostSendsCardExchangeMessage(gameState: GameState) {
        val cardExchangeOfLocalGuest = DwitchEngine(gameState).getCardsExchanges()
            .find { (playerId, _) -> playerId == PlayerGuestTest.LocalGuest.inGameId }!!.second

        val envelope = HostMessageFactory.createCardExchangeMessage(PlayerGuestTest.LocalGuest.inGameId, cardExchangeOfLocalGuest, ConnectionId(2))
        clientTestStub.serverSendsMessageToClient(envelope.message, true)
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
