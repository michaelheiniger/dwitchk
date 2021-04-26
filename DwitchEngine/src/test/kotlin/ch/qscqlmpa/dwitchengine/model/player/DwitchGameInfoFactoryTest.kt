package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameStateMutable
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

        val cardsPlayer1 = mutableListOf(
            Card.Clubs2,
            Card.Hearts3,
            Card.Diamonds4,
            Card.Spades6
        )
        val cardsPlayer2 = mutableListOf(
            Card.Diamonds2,
            Card.Spades10,
            Card.DiamondsAce,
            Card.SpadesKing
        )

        player1 = PlayerMutable(
            id = DwitchPlayerId(1),
            name = "Aragorn",
            cardsInHand = cardsPlayer1,
            rank = DwitchRank.President,
            status = DwitchPlayerStatus.Playing,
            dwitched = false,
            cardsForExchange = mutableSetOf()
        )

        player2 = PlayerMutable(
            id = DwitchPlayerId(2),
            name = "Gimli",
            cardsInHand = cardsPlayer2,
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
        @Test
        fun `last card played is blank when table is empty`() {
            gameState.cardsOnTable.clear()
            launchTest { gameInfo -> assertThat(gameInfo.lastCardPlayed).isEqualTo(Card.Blank) }
        }

        @Test
        fun `last card played is the last card on the table`() {
            gameState.cardsOnTable.clear()
            gameState.cardsOnTable.addAll(listOf(Card.ClubsAce, Card.Hearts5, Card.Spades6))
            launchTest { gameInfo -> assertThat(gameInfo.lastCardPlayed).isEqualTo(Card.Spades6) }
        }
    }

    @Nested
    inner class PlayerCanPlay {
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
    inner class CardCanPlayed {

        @BeforeEach
        fun setup() {
            player1 = player1.copy(
                cardsInHand = mutableListOf(
                    Card.Clubs2,
                    Card.Hearts3,
                    Card.Diamonds4,
                    Card.Spades6
                )
            )
            gameState.joker = CardName.Two
            gameState.cardsOnTable.clear()
            gameState.cardsOnTable.addAll(listOf(Card.ClubsAce, Card.Spades4))
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
            gameState.cardsOnTable.addAll(listOf(Card.ClubsAce, Card.Spades4))
            launchTest { gameInfo ->
                assertThat(gameInfo.playerInfos.getValue(player1.id).cardsInHand).contains(DwitchCardInfo(Card.Clubs2, true))
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
                gameState.cardsOnTable.toSet().mergeWith(
                    player1.cardsInHand().toSet(),
                    player2.cardsInHand().toSet(),
                    gameState.cardsInGraveyard.toSet()
                )
            )
        )

        assertions(DwitchGameInfoFactory(gameState.toGameState()).create())
    }
}
