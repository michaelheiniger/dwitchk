package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameStateMutable
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.utils.CollectionUtil.mergeWith
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DwitchGameInfoFactoryTest {

    private lateinit var player1: PlayerMutable
    private lateinit var player2: PlayerMutable
    private lateinit var gameState: GameStateMutable

    @BeforeEach
    fun setup() {
        player1 = PlayerMutable(
            id = DwitchPlayerId(1),
            name = "Aragorn",
            cardsInHand = mutableListOf(),
            rank = DwitchRank.President,
            status = DwitchPlayerStatus.Playing,
            dwitched = false,
            cardsForExchange = mutableSetOf()
        )

        player2 = PlayerMutable(
            id = DwitchPlayerId(2),
            name = "Gimli",
            cardsInHand = mutableListOf(),
            rank = DwitchRank.Asshole,
            status = DwitchPlayerStatus.Waiting,
            dwitched = false,
            cardsForExchange = mutableSetOf()
        )

        gameState = GameStateMutable(
            phase = DwitchGamePhase.RoundIsBeginning,
            players = mapOf(),
            playingOrder = mutableListOf(DwitchPlayerId(1), DwitchPlayerId(2)),
            currentPlayerId = DwitchPlayerId(1),
            activePlayers = mutableSetOf(DwitchPlayerId(1), DwitchPlayerId(2)),
            playersDoneForRound = mutableListOf(),
            playersWhoBrokeASpecialRule = mutableListOf(),
            joker = CardName.Two,
            dwitchGameEvent = null,
            cardsOnTable = mutableListOf(),
            cardsInDeck = mutableSetOf(),
            cardsInGraveyard = mutableListOf(),
        )
    }

    @Nested
    inner class LastCardPlayer {

        @BeforeEach
        fun setup() {
            player1 = player1.copy(cardsInHand = mutableListOf(Card.Clubs2, Card.Hearts3, Card.Diamonds4, Card.Spades6))
            player2 = player2.copy(cardsInHand = mutableListOf(Card.Diamonds2, Card.Spades10, Card.DiamondsAce, Card.SpadesKing))
        }

        @Test
        fun `last card played is null`() {
            gameState.cardsOnTable.clear()
            launchTest { gameInfo -> assertThat(gameInfo.lastCardPlayed).isNull() }
        }

        @Test
        fun `last card played is the last card on the table`() {
            gameState.cardsOnTable.clear()
            gameState.cardsOnTable.addAll(
                listOf(
                    PlayedCards(Card.ClubsAce),
                    PlayedCards(Card.Hearts5),
                    PlayedCards(Card.Spades7)
                )
            )
            launchTest { gameInfo -> assertThat(gameInfo.lastCardPlayed).isEqualTo(PlayedCards(Card.Spades7)) }
        }
    }

    @Nested
    inner class PlayerCanPlay {

        @BeforeEach
        fun setup() {
            player1 = player1.copy(cardsInHand = mutableListOf(Card.Clubs2, Card.Hearts3, Card.Diamonds4, Card.Spades6))
            player2 = player2.copy(cardsInHand = mutableListOf(Card.Diamonds2, Card.Spades10, Card.DiamondsAce, Card.SpadesKing))
        }

        @Test
        fun `player can play when its status is Playing`() {
            player1 = player1.copy(status = DwitchPlayerStatus.Playing)
            launchTest { gameInfo -> assertThat(gameInfo.playerInfos.getValue(player1.id).canPlay).isTrue }
        }

        @Test
        fun `player cannot play when its status is Waiting`() {
            player2 = player2.copy(status = DwitchPlayerStatus.Waiting)
            launchTest { gameInfo -> assertThat(gameInfo.playerInfos.getValue(player2.id).canPlay).isFalse }
        }

        @Test
        fun `player cannot play when its status is Done`() {
            player2 = player2.copy(status = DwitchPlayerStatus.Done)
            launchTest { gameInfo -> assertThat(gameInfo.playerInfos.getValue(player2.id).canPlay).isFalse }
        }
    }

    @Nested
    inner class OneCardCanPlayed {

        @BeforeEach
        fun setup() {
            player1 = player1.copy(cardsInHand = mutableListOf(Card.Clubs2, Card.Hearts3, Card.Diamonds4, Card.Spades6))
            player2 = player2.copy(cardsInHand = mutableListOf(Card.Diamonds2, Card.Spades10, Card.DiamondsAce, Card.SpadesKing))
            gameState.joker = CardName.Two
            gameState.cardsOnTable.clear()
            gameState.cardsOnTable.addAll(listOf(PlayedCards(Card.ClubsAce), PlayedCards(Card.Spades4)))
        }

        @Test
        fun `card cannot be played when its value is lower than the value of the last card on the table`() {
            launchTest { gameInfo ->
                assertThat(gameInfo.playerInfos.getValue(player1.id).cardsInHand).contains(
                    DwitchCardInfo(Card.Hearts3, false)
                )
            }
        }

        @Test
        fun `card can be played when its value equals the value of the last card on the table`() {
            launchTest { gameInfo ->
                assertThat(gameInfo.playerInfos.getValue(player1.id).cardsInHand).contains(
                    DwitchCardInfo(Card.Diamonds4, true)
                )
            }
        }

        @Test
        fun `card can be played when its value is higher than the value of the last card on the table`() {
            launchTest { gameInfo ->
                assertThat(gameInfo.playerInfos.getValue(player1.id).cardsInHand).contains(
                    DwitchCardInfo(Card.Spades6, true)
                )
            }
        }

        @Test
        fun `card can be played when it is the joker regardless of its value`() {
            player1 = player1.copy(cardsInHand = mutableListOf(Card.Clubs2, Card.Hearts3, Card.Diamonds4, Card.Spades6))
            gameState.joker = CardName.Two
            gameState.cardsOnTable.clear()
            gameState.cardsOnTable.addAll(listOf(PlayedCards(Card.Clubs3), PlayedCards(Card.Spades4)))
            launchTest { gameInfo ->
                assertThat(gameInfo.playerInfos.getValue(player1.id).cardsInHand).contains(DwitchCardInfo(Card.Clubs2, true))
            }
        }
    }

    @Nested
    inner class MultipleCardsCanPlayed {

        @BeforeEach
        fun setup() {
            player1 = player1.copy(
                cardsInHand = mutableListOf(
                    Card.Clubs2,
                    Card.Hearts2,
                    Card.Spades3,
                    Card.Diamonds3,
                    Card.Hearts3,
                    Card.Diamonds4,
                    Card.Diamonds5,
                    Card.Spades5,
                    Card.Diamonds7,
                    Card.Hearts8,
                    Card.Spades8,
                    Card.Diamonds8
                )
            )
            player2 = player2.copy(cardsInHand = mutableListOf(Card.Diamonds2, Card.Spades10, Card.DiamondsAce, Card.SpadesKing))
            gameState.joker = CardName.Two
            gameState.cardsOnTable.clear()
            gameState.cardsOnTable.addAll(listOf(PlayedCards(Card.Hearts5, Card.Clubs5)))
        }

        @Test
        fun `only cards with multiplicity equal of higher than last card played can be selected`() {
            launchTest { gameInfo ->
                assertThat(gameInfo.playerInfos.getValue(player1.id).cardsInHand).contains(
                    DwitchCardInfo(Card.Clubs2, selectable = true),
                    DwitchCardInfo(Card.Hearts2, selectable = true),
                    DwitchCardInfo(Card.Spades3, selectable = false),
                    DwitchCardInfo(Card.Diamonds3, selectable = false),
                    DwitchCardInfo(Card.Hearts3, selectable = false),
                    DwitchCardInfo(Card.Diamonds4, selectable = false),
                    DwitchCardInfo(Card.Diamonds5, selectable = true),
                    DwitchCardInfo(Card.Spades5, selectable = true),
                    DwitchCardInfo(Card.Diamonds7, selectable = false),
                    DwitchCardInfo(Card.Hearts8, selectable = true),
                    DwitchCardInfo(Card.Spades8, selectable = true),
                    DwitchCardInfo(Card.Diamonds8, selectable = true)
                )
            }
        }
    }

    private fun launchTest(assertions: (DwitchGameInfo) -> Unit) {
        gameState = gameState.copy(
            players = mapOf(
                DwitchPlayerId(1) to player1,
                DwitchPlayerId(2) to player2
            )
        )

        gameState.cardsInDeck.clear()
        gameState.cardsInDeck.addAll(
            CardUtil.getAllCardsExcept(
                gameState.cardsOnTable.flatMap(PlayedCards::cards).toSet().mergeWith(
                    player1.cardsInHand().toSet(),
                    player2.cardsInHand().toSet(),
                    gameState.cardsInGraveyard.flatMap(PlayedCards::cards).toSet()
                )
            )
        )

        assertions(DwitchGameInfoFactory(gameState.toGameState()).create())
    }
}
