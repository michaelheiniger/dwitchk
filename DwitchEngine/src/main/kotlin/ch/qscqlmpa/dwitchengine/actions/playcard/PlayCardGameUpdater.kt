package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.DwitchPlayerAction
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

internal class PlayCardGameUpdater(
    currentGameState: DwitchGameState
) : GameUpdaterBase(currentGameState) {

    private lateinit var playedCards: PlayedCards
    private var dwitchedPlayerId: DwitchPlayerId? = null

    fun clearTable() {
        gameStateMutable.moveCardsFromTableToGraveyard()
    }

    fun takeCardsFromHandAndPutOnTable(playerId: DwitchPlayerId, playedCards: PlayedCards) {
        gameStateMutable.removeCardsFromHand(playerId, playedCards)
        gameStateMutable.addCardsToTable(playedCards)
        this.playedCards = playedCards
    }

    fun dwitchPlayer(playerId: DwitchPlayerId) {
        gameStateMutable.dwitchPlayer(playerId)
        dwitchedPlayerId = playerId
    }

    override fun buildUpdatedGameState(): DwitchGameState {
        gameStateMutable.lastPlayerAction = DwitchPlayerAction.PlayCards(
            playerId = currentPlayerId,
            playedCards = playedCards,
            dwitchedPlayedId = dwitchedPlayerId
        )
        return super.buildUpdatedGameState()
    }
}
