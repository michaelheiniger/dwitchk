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
    var dwitchGameEvent: DwitchGameEvent?,
    val cardsOnTable: MutableList<Card>,
    val cardsInDeck: MutableList<Card>,
    val cardGraveyard: MutableList<Card>
) {

    fun addCardToHand(playerId: DwitchPlayerId, card: Card) {
        player(playerId).addCardToHand(card)
    }

    fun addCardsToHand(playerId: DwitchPlayerId, cards: List<Card>) {
        cards.forEach { card -> addCardToHand(playerId, card) }
    }

    fun removeCardFromHand(playerId: DwitchPlayerId, card: Card) {
        player(playerId).removeCardFromHand(card)
    }

    fun removeAllCardsForCardExchange(playerId: DwitchPlayerId) {
        player(playerId).removeAllCardsForExchange()
    }

    fun removeTopCardFromDeck(): Card {
        return cardsInDeck.removeAt(0)
    }

    fun dwitchPlayer(playerId: DwitchPlayerId) {
        player(playerId).dwitched = true
    }

    fun undwitchAllPlayers() {
        allPlayers().forEach { p -> p.dwitched = false }
    }

    fun addCardToTable(card: Card) {
        cardsOnTable.add(card)
    }

    fun setPlayerRank(playerId: DwitchPlayerId, rank: DwitchRank) {
        player(playerId).rank = rank
    }

    fun setPlayerState(playerId: DwitchPlayerId, state: DwitchPlayerStatus) {
        player(playerId).state = state
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
            dwitchGameEvent,
            cardsOnTable,
            cardsInDeck,
            cardGraveyard
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
                gameState.dwitchGameEvent,
                gameState.cardsOnTable.toMutableList(),
                gameState.cardsInDeck.toMutableList(),
                gameState.cardsInGraveyard.toMutableList()
            )
        }
    }
}
