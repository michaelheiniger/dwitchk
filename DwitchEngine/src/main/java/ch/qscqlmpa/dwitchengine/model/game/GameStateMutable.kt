package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.player.*

internal data class GameStateMutable(
    var phase: GamePhase,
    val players: Map<PlayerInGameId, PlayerMutable>,
    val playingOrder: MutableList<PlayerInGameId>,
    var currentPlayerId: PlayerInGameId,
    val activePlayers: MutableSet<PlayerInGameId>,
    val playersDoneForRound: MutableList<PlayerDone>,
    var joker: CardName,
    var gameEvent: GameEvent?,
    val cardsOnTable: MutableList<Card>,
    val cardsInDeck: MutableList<Card>,
    val cardGraveyard: MutableList<Card>
) {

    fun addCardToHand(playerId: PlayerInGameId, card: Card) {
        player(playerId).addCardToHand(card)
    }

    fun removeCardFromHand(playerId: PlayerInGameId, card: Card) {
        player(playerId).removeCardFromHand(card)
    }

    fun removeTopCardFromDeck(): Card {
        return cardsInDeck.removeAt(0)
    }

    fun dwitchPlayer(playerId: PlayerInGameId) {
        player(playerId).dwitched = true
    }

    fun undwitchAllPlayers() {
        allPlayers().forEach { p -> p.dwitched = false }
    }

    fun addCardToTable(card: Card) {
        cardsOnTable.add(card)
    }

    fun setPlayerRank(playerId: PlayerInGameId, rank: Rank) {
        player(playerId).rank = rank
    }

    fun setPlayerState(playerId: PlayerInGameId, state: PlayerState) {
        player(playerId).state = state
    }

    fun removePlayerFromActivePlayers(playerId: PlayerInGameId) {
        val removalSuccessful = activePlayers.remove(playerId)
        if (!removalSuccessful) {
            throw IllegalStateException("Player ${player(playerId)} cannot be removed from active players since it is not an active player.")
        }
    }

    fun allPlayers(): List<PlayerMutable> {
        return players.values.toList()
    }

    fun moveCardsFromTableToGraveyard() {
        cardGraveyard.addAll(cardsOnTable)
        clearTable()
    }

    fun clearTable() {
        cardsOnTable.clear()
    }

    fun clearGraveyard() {
        cardGraveyard.clear()
    }

    fun setCardsOnTable(card: Card) {
        clearTable()
        cardsOnTable.add(card)
    }

    fun addDonePlayer(playerId: PlayerInGameId, lastCardPlayedIsJoker: Boolean) {
        playersDoneForRound.add(PlayerDone(playerId, lastCardPlayedIsJoker))
    }

    fun toGameState(): GameState {

        val immutablePlayers = players.map { entry -> entry.key to entry.value.toPlayer() }.toMap()

        return GameState(
            phase,
            immutablePlayers,
            playingOrder,
            currentPlayerId,
            activePlayers,
            playersDoneForRound,
            joker,
            gameEvent,
            cardsOnTable,
            cardsInDeck,
            cardGraveyard
        )
    }

    private fun player(inGameId: PlayerInGameId): PlayerMutable {
        return players.getValue(inGameId)
    }

    companion object {
        internal fun fromGameState(gameState: GameState): GameStateMutable {
            val players = mutableMapOf<PlayerInGameId, PlayerMutable>()
            for (entry in gameState.players.entries) {
                players[entry.key] = PlayerMutable.fromPlayer(entry.value)
            }

            return GameStateMutable(
                gameState.phase,
                players.toMap(),
                gameState.playingOrder.toMutableList(),
                gameState.currentPlayerId,
                gameState.activePlayers.toMutableSet(),
                gameState.playersDoneForRound.toMutableList(),
                gameState.joker,
                gameState.gameEvent,
                gameState.cardsOnTable.toMutableList(),
                gameState.cardsInDeck.toMutableList(),
                gameState.cardsInGraveyard.toMutableList()
            )
        }
    }
}