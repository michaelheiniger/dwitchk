package ch.qscqlmpa.dwitch.e2e

import ch.qscqlmpa.dwitch.e2e.base.BaseGuestTest
import org.junit.Ignore

@Ignore
class GameRoomAsGuestTest : BaseGuestTest() {

//    private var rankForPlayer: Map<Int, Rank> = mapOf(
//        0 to Rank.President,
//        1 to Rank.Asshole
//    )
//    private var cardsForPlayer: Map<Int, List<Card>> = mapOf(
//        0 to listOf(Card.Hearts5, Card.Clubs3),
//        1 to listOf(Card.Spades6, Card.Spades4)
//    )
//
//    @Test
//    fun goToGameRoomScreen() {
//        launch()
//
//        goToGameRoom()
//
//        GameRoomUiUtil.assertCardInHand(0, Card.Spades6)
//        GameRoomUiUtil.assertCardInHand(1, Card.Spades4)
//        GameRoomUiUtil.assertCardOnTable(Card.Clubs2)
//    }
//
//    @Test
//    fun playACard() {
//        launch()
//
//        goToGameRoom()
//
//        GameRoomUiUtil.assertCardInHand(0, Card.Spades6)
//        GameRoomUiUtil.assertCardInHand(1, Card.Spades4)
//        GameRoomUiUtil.assertCardOnTable(Card.Clubs2)
//
//        GameRoomUiUtil.playCard(0)
//
//        val gameStateUpdatedMessage = waitForNextMessageSentByLocalGuest() as Message.GameStateUpdatedMessage
//        assertThat(gameStateUpdatedMessage.gameState.cardsOnTable).isEqualTo(listOf(Card.Clubs2, Card.Spades6))
//
//        GameRoomUiUtil.assertCardInHand(0, Card.Spades4)
//        GameRoomUiUtil.assertCardOnTable(Card.Spades6)
//
//        GameRoomUiUtil.assertCanPickACard(false)
//        GameRoomUiUtil.assertCanPassTurn(false)
//    }
//
//    @Test
//    fun showEndOfRoundResults() {
//        launch()
//
//        cardsForPlayer = mapOf(
//            0 to listOf(Card.Hearts5),
//            1 to listOf(Card.Spades6)
//        )
//
//        goToGameRoom()
//
//        GameRoomUiUtil.playCard(0)
//
//        val message = waitForNextMessageSentByLocalGuest()
//        assertThat(message).isInstanceOf(Message.GameStateUpdatedMessage::class.java)
//
//        UiUtil.assertControlTextContent(R.id.mainTextTv, Matchers.containsString("$hostName: Asshole"))
//        UiUtil.assertControlTextContent(R.id.mainTextTv, Matchers.containsString("Boromir: President"))
//
//        closeEndOfRoundDialog()
//
//        GameRoomUiUtil.assertGameRoomIsDisplayed()
//    }
//
//    @Test
//    fun cardExchange() {
//        launch()
//
//        cardsForPlayer = mapOf(
//            0 to listOf(Card.Hearts5),
//            1 to listOf(Card.Spades6)
//        )
//
//        goToGameRoom()
//
//        GameRoomUiUtil.playCard(0)
//
//        val gameStateUpdatedMessage = waitForNextMessageSentByLocalGuest() as Message.GameStateUpdatedMessage
//
//        closeEndOfRoundDialog()
//
//        hostStartsNewRound(gameStateUpdatedMessage.gameState)
//
//        dudeWaitASec()
//
//        GameRoomUiUtil.assertCardInHand(0, Card.Spades6)
//        GameRoomUiUtil.assertCardInHand(1, Card.Spades4)
//        GameRoomUiUtil.assertCardInHand(2, Card.Diamonds4)
//        GameRoomUiUtil.assertCardInHand(3, Card.Clubs10)
//
//        GameRoomUiUtil.chooseCardForExchange(1)
//        GameRoomUiUtil.chooseCardForExchange(1)
//
//        GameRoomUiUtil.confirmCardsChoiceForExchange()
//
//        val cardsForExchangeMessageMessage = waitForNextMessageSentByLocalGuest() as Message.CardsForExchangeMessage
//        assertThat(cardsForExchangeMessageMessage.playerId).isEqualTo(PlayerGuestTest.LocalGuest.id)
//        assertThat(cardsForExchangeMessageMessage.cards).isEqualTo(setOf(Card.Spades4, Card.Diamonds4))
//
//        dudeWaitAMillisSec()
//
//        GameRoomUiUtil.assertGameRoomIsDisplayed()
//    }
//
//    @Test
//    fun gameOver() {
//        launch()
//
//        goToGameRoom()
//
//        clientTestStub.serverSendsMessageToClient(Message.GameOverMessage, false)
//
//        dudeWaitASec()
//
//        closeEndOfRoundDialog()
//
//        UiUtil.elementIsDisplayed(R.id.gameListTv)
//    }
//
//    private fun goToGameRoom() {
//        advertiseGame()
//
//        goToWaitingRoom()
//
//        hostSendsJoinGameAck()
//        hostSendsInitialWaitingRoomUpdate()
//
//        dudeWaitASec()
//
//        UiUtil.clickOnButton(R.id.localPlayerReadyCkb)
//
//        clientTestStub.serverSendsMessageToClient(Message.LaunchGameMessage(createGameState()), false)
//
//        dudeWaitASec()
//
//        GameRoomUiUtil.assertGameRoomIsDisplayed()
//    }
//
//    private fun createGameState(): GameState {
//        val players = listOf(
//            TestEntityFactory.createHostPlayer(dwitchId = PlayerGuestTest.Host.id),
//            TestEntityFactory.createGuestPlayer1(dwitchId = PlayerGuestTest.LocalGuest.id)
//        )
//        val initialGameSetup = DeterministicInitialGameSetup(cardsForPlayer, rankForPlayer)
//        return createNewGame(players.map(Player::toPlayerInfo), initialGameSetup)
//    }
//
//    private fun hostStartsNewRound(gameState: GameState): GameState {
//        val cardDealerFactory = DeterministicCardDealerFactory()
//        cardDealerFactory.setInstance(
//            DeterministicCardDealer(
//                mapOf(
//                    0 to listOf(Card.Hearts5, Card.Clubs3, Card.Spades6, Card.HeartsAce),
//                    1 to listOf(Card.Spades6, Card.Spades4, Card.Diamonds4, Card.Clubs10)
//                )
//            )
//        )
//        val newRoundGameState = ProdDwitchEngineFactory().create(gameState).startNewRound(cardDealerFactory)
//        val message = MessageFactory.createGameStateUpdatedMessage(newRoundGameState)
//        clientTestStub.serverSendsMessageToClient(message, false)
//
//        return newRoundGameState
//    }
//
//    private fun hostSendsInitialWaitingRoomUpdate() {
//        val message = Message.WaitingRoomStateUpdateMessage(
//            listOf(
//                PlayerWr(
//                    PlayerGuestTest.Host.id,
//                    PlayerGuestTest.Host.name,
//                    PlayerRole.HOST,
//                    PlayerConnectionState.CONNECTED,
//                    true
//                ),
//                PlayerWr(
//                    PlayerGuestTest.LocalGuest.id,
//                    PlayerGuestTest.LocalGuest.name,
//                    PlayerRole.GUEST,
//                    PlayerConnectionState.CONNECTED,
//                    false
//                ),
//            )
//        )
//        clientTestStub.serverSendsMessageToClient(message, false)
//    }
}