package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.*

class EngineTestGameStateBuilder {

    private val playersMap = mutableMapOf<DwitchPlayerId, DwitchPlayer>()

    private var cardsOnTable: List<Card> = emptyList()
    private var cardsInGraveyard: List<Card> = emptyList()

    private var dwitchGameEvent: DwitchGameEvent? = null
    private var joker: CardName = CardName.Two
    private lateinit var gamePhase: DwitchGamePhase
    private lateinit var localPlayer: DwitchPlayerId
    private lateinit var currentPlayer: DwitchPlayerId
    private var playersDoneForRound: List<DwitchPlayerId> = emptyList()

    private val playingOrder: MutableList<DwitchPlayerId> = mutableListOf()

    fun build(): DwitchGameState {
        val cardsTakenFromDeck = playersMap
            .map { (_, player) -> player.cardsInHand }
            .flatten()
            .toMutableList()
        cardsTakenFromDeck.addAll(cardsOnTable)
        cardsTakenFromDeck.addAll(cardsInGraveyard)

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
            CardUtil.getAllCardsExcept(cardsTakenFromDeck),
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

    fun setCardsdOnTable(vararg cardsOnTable: Card): EngineTestGameStateBuilder {
        this.cardsOnTable = listOf(*cardsOnTable)
        return this
    }

    fun setGraveyard(vararg cardsInGraveyard: Card): EngineTestGameStateBuilder {
        this.cardsInGraveyard = listOf(*cardsInGraveyard)
        return this
    }

    fun setLocalPlayer(id: DwitchPlayerId): EngineTestGameStateBuilder {
        this.localPlayer = id
        return this
    }

    fun setCurrentPlayer(id: DwitchPlayerId): EngineTestGameStateBuilder {
        this.currentPlayer = id
        return this
    }

    fun setPlayersDoneForRound(list: List<DwitchPlayerId>) {
        playersDoneForRound = list
    }

    fun setPlayerCards(id: DwitchPlayerId, cards: List<Card>) {
        playersMap[id] = playersMap.getValue(id).copy(cardsInHand = cards)
    }

    fun addPlayerToGame(
        player: DwitchPlayerOnboardingInfo,
        state: DwitchPlayerStatus,
        rank: DwitchRank,
        cardsInHand: List<Card> = emptyList(),
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): EngineTestGameStateBuilder {
        playersMap[player.id] = DwitchPlayer(
            player.id,
            player.name,
            cardsInHand,
            rank,
            state,
            dwitched,
            hasPickedCard

        )
        playingOrder.add(player.id)
        return this
    }
}
