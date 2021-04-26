package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.SpecialRuleBreaker
import ch.qscqlmpa.dwitchengine.utils.CollectionUtil.shiftRightByN
import kotlinx.serialization.Serializable

@Serializable
data class DwitchGameState(
    val phase: DwitchGamePhase,
    val players: Map<DwitchPlayerId, DwitchPlayer>,
    val playingOrder: List<DwitchPlayerId>,
    val currentPlayerId: DwitchPlayerId,
    val activePlayers: Set<DwitchPlayerId>,
    val playersDoneForRound: List<DwitchPlayerId>,
    val playersWhoBrokeASpecialRule: List<SpecialRuleBreaker>,
    val joker: CardName,
    val dwitchGameEvent: DwitchGameEvent?,
    val cardsOnTable: List<Card>,
    val cardsInDeck: Set<Card>,
    val cardsInGraveyard: List<Card>
) {

    init {
        performSanityChecks()
    }

    val phaseIsPlayable
        get(): Boolean {
            return phase.isOneOf(DwitchGamePhase.RoundIsBeginning, DwitchGamePhase.RoundIsOnGoing)
        }

    fun lastCardOnTable(): Card? {
        return cardsOnTable.lastOrNull()
    }

    fun player(dwitchId: DwitchPlayerId): DwitchPlayer {
        return players.getValue(dwitchId)
    }

    fun currentPlayer(): DwitchPlayer {
        return player(currentPlayerId)
    }

    fun numberOfActivePlayers(): Int {
        return activePlayers.size
    }

    /**
     * Returns a list of all players 'Waiting' in playing order, starting with the player following the current player.
     * Note: The current player is never returned since it cannot be 'Waiting' (it can only be 'Playing' when playing).
     */
    fun waitingPlayersInOrder(): List<DwitchPlayer> {
        return activePlayersInPlayingOrderAfterCurrentPlayer()
            .filter { player -> player.status == DwitchPlayerStatus.Waiting }
    }

    fun nextWaitingPlayer(): DwitchPlayer? {
        return waitingPlayersInOrder().firstOrNull()
    }

    /**
     * Returns a list of all active players (i.e. 'Waiting', 'TurnPassed' or 'Playing'), starting with the current player.
     */
    fun activePlayersInPlayingOrderAfterCurrentPlayer(): List<DwitchPlayer> {
        val localPlayerIndex = playingOrder.indexOf(currentPlayerId)
        return playingOrder.shiftRightByN(-localPlayerIndex)
            .filter { id -> activePlayers.contains(id) }
            .map { id -> player(id) }
    }

    private fun performSanityChecks() {
        playersDoneForRound.forEach { id ->
            if (activePlayers.contains(id)) {
                throw IllegalStateException("A player in the 'players done' list cannot be an active player at the same time.")
            }
            if (players.getValue(id).status != DwitchPlayerStatus.Done) {
                throw IllegalStateException("A player in the 'players done' list cannot have a state different than Done")
            }
        }
        val allCardsInGame = listOf(
            cardsInDeck,
            players.map { (_, player) -> player.cardsInHand }.flatten(),
            players.map { (_, player) -> player.cardsForExchange }.flatten(),
            cardsOnTable,
            cardsInGraveyard
        ).flatten()

        // For debug
//        allCardsInGame
//            .sortedWith { o1, o2 -> o1.id.value.compareTo(o2.id.value) }
//            .forEachIndexed { index, c -> Logger.debug { "index: $index, card: $c" } }

        if (allCardsInGame.toSet().size != CardUtil.deckSize) {
            throw IllegalStateException("There must be exactly ${CardUtil.deckSize} cards in the game, actual value: ${allCardsInGame.size}")
        }

        players.values.forEach { p ->
            if (p.isTheOnePlaying) {
                require(p.id == currentPlayerId)
                { "The player 'Playing' must always be the current player (player: ${p.id}, current player: $currentPlayerId." }
            }
        }
    }
}
