package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.*

class EngineTestGameStateBuilder {

    private val playersMap = mutableMapOf<DwitchPlayerId, DwitchPlayer>()

    private var cardsOnTable: List<PlayedCards> = emptyList()
    private var cardsInGraveyard: List<PlayedCards> = emptyList()

    private var dwitchGameEvent: DwitchGameEvent? = null
    private var joker: CardName = CardName.Two
    private lateinit var gamePhase: DwitchGamePhase
    private lateinit var currentPlayer: DwitchPlayerId
    private var playersDoneForRound: List<DwitchPlayerId> = emptyList()

    private val playingOrder: MutableList<DwitchPlayerId> = mutableListOf()

    fun build(): DwitchGameState {
        val cardsTakenFromDeck = playersMap.flatMap { (_, player) -> player.cardsInHand }.toMutableList()
        cardsTakenFromDeck.addAll(cardsOnTable.flatMap(PlayedCards::cards))
        cardsTakenFromDeck.addAll(cardsInGraveyard.flatMap(PlayedCards::cards))

        val activePlayers = playersMap
            .filter { (_, player) -> player.status != DwitchPlayerStatus.Done }
            .map { (_, player) -> player.id }
            .toSet()

//        cardsTakenFromDeck.forEach { c -> Logger.debug { c } }

        return DwitchGameState(
            gamePhase,
            playersMap,
            playingOrder,
            currentPlayer,
            activePlayers,
            playersDoneForRound,
            emptyList(),
            joker,
            dwitchGameEvent,
            cardsOnTable,
            CardUtil.getAllCardsExcept(cardsTakenFromDeck).toSet(),
            cardsInGraveyard
        )
    }

    fun setGamePhase(gamePhase: DwitchGamePhase): EngineTestGameStateBuilder {
        this.gamePhase = gamePhase
        return this
    }

    fun setGameEvent(dwitchGameEvent: DwitchGameEvent): EngineTestGameStateBuilder {
        this.dwitchGameEvent = dwitchGameEvent
        return this
    }

    fun setJoker(cardName: CardName): EngineTestGameStateBuilder {
        this.joker = cardName
        return this
    }

    fun setCardsdOnTable(vararg cardsOnTable: PlayedCards): EngineTestGameStateBuilder {
        this.cardsOnTable = listOf(*cardsOnTable)
        return this
    }

    fun setCardGraveyard(vararg cardsInGraveyard: PlayedCards): EngineTestGameStateBuilder {
        this.cardsInGraveyard = listOf(*cardsInGraveyard)
        return this
    }

    fun setCurrentPlayer(id: DwitchPlayerId): EngineTestGameStateBuilder {
        this.currentPlayer = id
        return this
    }

    fun setPlayerStatus(id: DwitchPlayerId, status: DwitchPlayerStatus): EngineTestGameStateBuilder {
        playersMap[id] = playersMap.getValue(id).copy(status = status)
        return this
    }

    fun updatePlayer(id: DwitchPlayerId, rank: DwitchRank, vararg cards: Card): EngineTestGameStateBuilder {
        playersMap[id] = playersMap.getValue(id).copy(rank = rank, cardsInHand = listOf(*cards))
        return this
    }

    fun setPlayersDoneForRound(list: List<DwitchPlayerId>): EngineTestGameStateBuilder {
        playersDoneForRound = list
        return this
    }

    fun setPlayerCards(id: DwitchPlayerId, vararg cards: Card): EngineTestGameStateBuilder {
        playersMap[id] = playersMap.getValue(id).copy(cardsInHand = listOf(*cards))
        return this
    }

    fun addPlayerToGame(
        player: DwitchPlayerOnboardingInfo,
        state: DwitchPlayerStatus,
        rank: DwitchRank,
        cardsInHand: List<Card> = emptyList(),
        dwitched: Boolean = false
    ): EngineTestGameStateBuilder {
        playersMap[player.id] = DwitchPlayer(
            player.id,
            player.name,
            cardsInHand,
            rank,
            state,
            dwitched
        )
        playingOrder.add(player.id)
        return this
    }
}
