package ch.qscqlmpa.dwitchengine.actions.startnewround

import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.PlayingOrderRankComparator
import ch.qscqlmpa.dwitchengine.rules.PlayingOrder

internal class StartNewRound constructor(
    private val gameState: StartNewRoundState,
    private val gameUpdater: StartNewRoundGameUpdater,
    private val cardDealerFactory: CardDealerFactory
) {

    fun getUpdatedGameState(): GameState {
        gameUpdater.setGamePhase(GamePhase.RoundIsBeginningWithCardExchange)
        gameUpdater.clearDonePlayersInFinishingOrder()
        gameUpdater.joker(CardName.Two)
        gameUpdater.undwitchAllPlayers()
        gameUpdater.resetGameEvent()
        setPlayerStates()
        setPlayingOrder()
        gameUpdater.activePlayers(gameState.getAllPlayersId())
        dealCards()
        gameUpdater.updateCurrentPlayer(gameState.asshole())
        return gameUpdater.buildUpdatedGameState()
    }

    private fun setPlayingOrder() {
        gameUpdater.playingOrder(PlayingOrder.getPlayingOrder(gameState.getAllPlayers()))
    }

    private fun setPlayerStates() {
        gameState.getAllPlayersId()
            .forEach { id -> gameUpdater.setPlayerState(id, PlayerStatus.Waiting) }
        gameUpdater.setPlayerState(gameState.asshole(), PlayerStatus.Playing)
    }

    private fun dealCards() {
        gameUpdater.clearTable()
        gameUpdater.clearGraveyard()
        val cardDealer = cardDealerFactory.getCardDealer(gameState.numPlayersTotal())
        gameState.getAllPlayers()
            .sortedWith(PlayingOrderRankComparator())
            .map(Player::id)
            .forEachIndexed { index, id ->
                gameUpdater.cardsInHandOfPlayer(id, cardDealer.getCardsForPlayer(index))
            }

        val remainingCards = cardDealer.getRemainingCards().toMutableList()
        gameUpdater.setFirstCardOnTable(remainingCards.removeAt(0))
        gameUpdater.cardsInDeck(remainingCards)
    }
}