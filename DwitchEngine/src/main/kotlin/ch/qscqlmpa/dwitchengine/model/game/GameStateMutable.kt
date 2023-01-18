package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.player.*

internal data class GameStateMutable(
    var phase: DwitchGamePhase,
    val players: Map<DwitchPlayerId, PlayerMutable>,
    val playingOrder: MutableList<DwitchPlayerId>,
    var currentPlayerId: DwitchPlayerId,
    val activePlayers: MutableSet<DwitchPlayerId>,
    val playersDoneForRound: MutableList<DwitchPlayerId>,
    val playersWhoBrokeASpecialRule: MutableList<SpecialRuleBreaker>,
    var joker: CardName,
    var lastPlayerAction: DwitchPlayerAction?,
    val cardsOnTable: MutableList<PlayedCards>,
    val cardsInDeck: MutableSet<Card>,
    val cardsInGraveyard: MutableList<PlayedCards>
) {

    fun addCardToHand(playerId: DwitchPlayerId, card: Card) {
        player(playerId).addCardToHand(card)
    }

    fun addCardsToHand(playerId: DwitchPlayerId, cards: List<Card>) {
        cards.forEach { card -> addCardToHand(playerId, card) }
    }

    fun removeCardsFromHand(playerId: DwitchPlayerId, playedCards: PlayedCards) {
        val player = player(playerId)
        playedCards.cards.forEach { card -> player.removeCardFromHand(card) }
    }

    fun removeAllCardsForCardExchange(playerId: DwitchPlayerId) {
        player(playerId).removeAllCardsForExchange()
    }

    fun dwitchPlayer(playerId: DwitchPlayerId) {
        player(playerId).dwitched = true
    }

    fun undwitchAllPlayers() {
        allPlayers().forEach { p -> p.dwitched = false }
    }

    fun addCardsToTable(cards: PlayedCards) {
        cardsOnTable.add(cards)
    }

    fun setPlayerRank(playerId: DwitchPlayerId, rank: DwitchRank) {
        player(playerId).rank = rank
    }

    fun setPlayerState(playerId: DwitchPlayerId, state: DwitchPlayerStatus) {
        player(playerId).status = state
    }

    fun removePlayerFromActivePlayers(playerId: DwitchPlayerId) {
        val removalSuccessful = activePlayers.remove(playerId)
        if (!removalSuccessful) {
            throw IllegalStateException("Player ${player(playerId)} cannot be removed from active players since it is not an active player.")
        }
    }

    fun allPlayers(): List<PlayerMutable> {
        return players.values.toList()
    }

    fun moveCardsFromTableToGraveyard() {
        cardsInGraveyard.addAll(cardsOnTable)
        clearTable()
    }

    fun clearTable() {
        cardsOnTable.clear()
    }

    fun clearGraveyard() {
        cardsInGraveyard.clear()
    }

    fun addDonePlayer(playerId: DwitchPlayerId, lastCardPlayedIsJoker: Boolean) {
        playersDoneForRound.add(playerId)
        if (lastCardPlayedIsJoker) {
            playersWhoBrokeASpecialRule.add(SpecialRuleBreaker.FinishWithJoker(playerId))
        }
    }

    fun addCardsForExchange(playerId: DwitchPlayerId, cards: Set<Card>) {
        player(playerId).removeCardsFromHand(cards)
        player(playerId).cardsForExchange.addAll(cards)
    }

    fun toGameState(): DwitchGameState {

        val immutablePlayers = players.map { entry -> entry.key to entry.value.toPlayer() }.toMap()

        return DwitchGameState(
            phase,
            immutablePlayers,
            playingOrder,
            currentPlayerId,
            activePlayers,
            playersDoneForRound,
            playersWhoBrokeASpecialRule,
            joker,
            lastPlayerAction,
            cardsOnTable,
            cardsInDeck,
            cardsInGraveyard
        )
    }

    private fun player(dwitchId: DwitchPlayerId): PlayerMutable {
        return players.getValue(dwitchId)
    }

    companion object {
        internal fun fromGameState(gameState: DwitchGameState): GameStateMutable {
            val players = mutableMapOf<DwitchPlayerId, PlayerMutable>()
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
                gameState.playersWhoBrokeASpecialRule.toMutableList(),
                gameState.joker,
                gameState.lastPlayerAction,
                gameState.cardsOnTable.toMutableList(),
                gameState.cardsInDeck.toMutableSet(),
                gameState.cardsInGraveyard.toMutableList()
            )
        }
    }
}
