package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.SpecialRuleBreaker
import ch.qscqlmpa.dwitchengine.utils.ListUtil.shiftRightByN
import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val phase: GamePhase,
    val players: Map<PlayerDwitchId, Player>,
    val playingOrder: List<PlayerDwitchId>,
    val currentPlayerId: PlayerDwitchId,
    val activePlayers: Set<PlayerDwitchId>,
    val playersDoneForRound: List<PlayerDwitchId>,
    val playersWhoBrokeASpecialRule: List<SpecialRuleBreaker>,
    val joker: CardName,
    val gameEvent: GameEvent?,
    val cardsOnTable: List<Card>,
    val cardsInDeck: List<Card>,
    val cardsInGraveyard: List<Card>
) {

    init {
        performSanityChecks()
    }

    val phaseIsPlayable
        get(): Boolean {
            return phase.isOneOf(GamePhase.RoundIsBeginning, GamePhase.RoundIsOnGoing)
        }

    fun lastCardOnTable(): Card? {
        return cardsOnTable.lastOrNull()
    }

    fun player(dwitchId: PlayerDwitchId): Player {
        return players.getValue(dwitchId)
    }

    fun currentPlayer(): Player {
        return player(currentPlayerId)
    }

    fun numberOfActivePlayers(): Int {
        return activePlayers.size
    }

    fun waitingPlayerInOrderAfterLocalPlayer(): List<Player> {
        return activePlayersInPlayingOrderAfterLocalPlayer()
            .filter { player -> player.status == PlayerStatus.Waiting }
    }

    fun nextWaitingPlayer(): Player? {
        return waitingPlayerInOrderAfterLocalPlayer().firstOrNull()
    }

    fun activePlayersInPlayingOrderAfterLocalPlayer(): List<Player> {
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
            if (players.getValue(id).status != PlayerStatus.Done) {
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
    }
}
