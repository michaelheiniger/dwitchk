package ch.qscqlmpa.dwitchengine.actions.startnewround

import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GameInfo
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.rules.PlayingOrder

internal class StartNewRound constructor(
    private val gameState: StartNewRoundState,
    private val gameUpdater: StartNewRoundGameUpdater,
    private val cardDealerFactory: CardDealerFactory
) {

    fun getUpdateGameState(): GameInfo {
        gameUpdater.setGamePhase(GamePhase.RoundIsBeginning)
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
        gameState.getAllPlayersId().forEach { id -> gameUpdater.setPlayerState(id, PlayerState.Waiting) }
        gameUpdater.setPlayerState(gameState.asshole(), PlayerState.Playing)
    }

    private fun dealCards() {
        gameUpdater.clearTable()
        gameUpdater.clearGraveyard()
        val cardDealer = cardDealerFactory.getCardDealer(gameState.numPlayersTotal())
        gameState.getAllPlayersId()
                .forEachIndexed { index, id -> gameUpdater.cardsInHandOfPlayer(id, cardDealer.getCardsForPlayer(index)) }

        val remainingCards = cardDealer.getRemainingCards().toMutableList()
        gameUpdater.setFirstCardOnTable(remainingCards.removeAt(0))
        gameUpdater.cardsInDeck(remainingCards)
    }

}