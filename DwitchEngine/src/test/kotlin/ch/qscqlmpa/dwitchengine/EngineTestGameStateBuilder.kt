package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.*

class EngineTestGameStateBuilder {

    private val playersMap = mutableMapOf<PlayerDwitchId, Player>()

    private var cardsOnTable: List<Card> = emptyList()
    private var cardsInGraveyard: List<Card> = emptyList()

    private var gameEvent: GameEvent? = null
    private var joker: CardName = CardName.Two
    private lateinit var gamePhase: GamePhase
    private lateinit var localPlayer: PlayerDwitchId
    private lateinit var currentPlayer: PlayerDwitchId
    private var playersDoneForRound: List<PlayerDwitchId> = emptyList()

    private val playingOrder: MutableList<PlayerDwitchId> = mutableListOf()

    fun build(): GameState {
        val cardsTakenFromDeck = playersMap
            .map { (_, player) -> player.cardsInHand }
            .flatten()
            .toMutableList()
        cardsTakenFromDeck.addAll(cardsOnTable)
        cardsTakenFromDeck.addAll(cardsInGraveyard)

        val activePlayers = playersMap
            .filter { (_, player) -> player.status != PlayerStatus.Done }
            .map { (_, player) -> player.id }
            .toSet()

//        cardsTakenFromDeck.forEach { c -> println(c) }

        return GameState(
            gamePhase,
            playersMap,
            playingOrder,
            currentPlayer,
            activePlayers,
            playersDoneForRound,
            emptyList(),
            joker,
            gameEvent,
            cardsOnTable,
            CardUtil.getAllCardsExcept(cardsTakenFromDeck),
            cardsInGraveyard
        )
    }

    fun setGamePhase(gamePhase: GamePhase): EngineTestGameStateBuilder {
        this.gamePhase = gamePhase
        return this
    }

    fun setGameEvent(gameEvent: GameEvent): EngineTestGameStateBuilder {
        this.gameEvent = gameEvent
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

    fun setLocalPlayer(id: PlayerDwitchId): EngineTestGameStateBuilder {
        this.localPlayer = id
        return this
    }

    fun setCurrentPlayer(id: PlayerDwitchId): EngineTestGameStateBuilder {
        this.currentPlayer = id
        return this
    }

    fun setPlayersDoneForRound(list: List<PlayerDwitchId>) {
        playersDoneForRound = list
    }

    fun setPlayerCards(id: PlayerDwitchId, cards: List<Card>) {
        playersMap[id] = playersMap.getValue(id).copy(cardsInHand = cards)
    }

    fun addPlayerToGame(
        player: PlayerOnboardingInfo,
        state: PlayerStatus,
        rank: Rank,
        cardsInHand: List<Card> = emptyList(),
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): EngineTestGameStateBuilder {
        playersMap[player.id] = Player(
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