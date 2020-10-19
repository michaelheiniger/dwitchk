package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerDone
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.utils.ListUtil.shiftRightByN
import kotlinx.serialization.Serializable

@Serializable
data class GameState(val phase: GamePhase,
                     val players: Map<PlayerInGameId, Player>,
                     val playingOrder: List<PlayerInGameId>,
                     val localPlayerId: PlayerInGameId,
                     val currentPlayerId: PlayerInGameId,
                     val activePlayers: Set<PlayerInGameId>,
                     val playersDoneForRound: List<PlayerDone>,
                     val joker: CardName,
                     val gameEvent: GameEvent?,
                     val cardsOnTable: List<Card>,
                     val cardsInDeck: List<Card>,
                     val cardsInGraveyard: List<Card>
) {

    init {
        performSanityChecks()
    }

    fun lastCardOnTable(): Card? {
        return cardsOnTable.lastOrNull()
    }

    fun player(inGameId: PlayerInGameId): Player {
        return players.getValue(inGameId)
    }

    fun localPlayer(): Player {
        return players.getValue(localPlayerId)
    }

    fun currentPlayer(): Player {
        return player(currentPlayerId)
    }

    fun numberOfActivePlayers(): Int {
        return activePlayers.size
    }

    fun waitingPlayerInOrderAfterLocalPlayer(): List<Player> {
        return activePlayersInPlayingOrderAfterLocalPlayer()
                .filter { player -> player.state == PlayerState.Waiting }
    }

    fun nextWaitingPlayer(): Player? {
        return waitingPlayerInOrderAfterLocalPlayer().firstOrNull()
    }

    fun activePlayersInPlayingOrderAfterLocalPlayer(): List<Player> {
        val localPlayerIndex = playingOrder.indexOf(localPlayerId)
        return playingOrder.shiftRightByN(-localPlayerIndex)
                .filter { id -> activePlayers.contains(id) }
                .map { id -> player(id) }
    }

    private fun performSanityChecks() {
        playersDoneForRound.forEach { playerDone ->
            if (activePlayers.contains(playerDone.playerId)) {
                throw IllegalStateException("A player in the 'players done' list cannot be an active player at the same time.")
            }
            if (players.getValue(playerDone.playerId).state != PlayerState.Done) {
                throw IllegalStateException("A player in the 'players done' list cannot have a state different than Done")
            }
        }
        val allCardsInGame = listOf(
                cardsInDeck,
                players.map { (_, player) -> player.cardsInHand }.flatten(),
                cardsOnTable,
                cardsInGraveyard
        ).flatten()

        // For debug
//        allCardsInGame
//                .sortedWith(Comparator { o1, o2 -> o1.id.value.compareTo(o2.id.value) })
//                .forEachIndexed { index, c -> println("index: $index, card: $c") }

        if (allCardsInGame.toSet().size != CardUtil.deckSize) {
            throw IllegalStateException("There must be exactly ${CardUtil.deckSize} cards in the game, actual value: ${allCardsInGame.size}")
        }
    }
}