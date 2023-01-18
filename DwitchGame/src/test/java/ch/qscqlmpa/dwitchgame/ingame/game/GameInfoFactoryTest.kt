package ch.qscqlmpa.dwitchgame.ingame.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchPlayerInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameInfoFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GameInfoFactoryTest : BaseUnitTest() {

    private val localPlayerId = DwitchPlayerId(1)

    private lateinit var gameInfo: DwitchGameInfo

    private val playersConnected = mutableMapOf(
        localPlayerId to true,
        DwitchPlayerId(2) to true,
        DwitchPlayerId(3) to true
    )

    @Nested
    inner class CreateGameDashboardInfo {

        @Test
        fun `Card is selectable if game engine says so and local player is the current player and is connected`() {
            // Given
            setupDwitchGameInfo()
            assertThat(gameInfo.currentPlayerId).isEqualTo(localPlayerId) // Local player is the current player
            assertThat(playersConnected[localPlayerId]).isTrue // Local player is connected

            // When
            val gameDashboardInfo = GameInfoFactory.createGameDashboardInfo(gameInfo, localPlayerId, playersConnected)
            val localPlayerCardsInHand = gameDashboardInfo.localPlayerDashboard.cardsInHand

            // Then

            // Club3 is not selectable because engine says it's not selectable (wrt game rules)
            assertThat(localPlayerCardsInHand).anyMatch { cardInfo -> cardInfo.card == Card.Clubs3 && !cardInfo.selectable }

            // Diamonds6 is selectable because engine says its selectable and local player is current player and is connected
            assertThat(localPlayerCardsInHand).anyMatch { cardInfo -> cardInfo.card == Card.Hearts6 && cardInfo.selectable }
        }

        @Test
        fun `Card is not selectable because local player is not the current player`() {
            // Given
            setupDwitchGameInfo(
                currentPlayerId = DwitchPlayerId(2)
            )
            assertThat(gameInfo.currentPlayerId).isNotEqualTo(localPlayerId) // Local player is NOT the current player
            assertThat(playersConnected[localPlayerId]).isTrue // Local player is connected

            // When
            val gameDashboardInfo = GameInfoFactory.createGameDashboardInfo(gameInfo, localPlayerId, playersConnected)
            val localPlayerCardsInHand = gameDashboardInfo.localPlayerDashboard.cardsInHand

            // Then

            // Club3 is not selectable because engine says it's not selectable (wrt game rules)
            assertThat(localPlayerCardsInHand).anyMatch { cardInfo -> cardInfo.card == Card.Clubs3 && !cardInfo.selectable }

            // Diamonds6 is not selectable because local player is not current player
            assertThat(localPlayerCardsInHand).anyMatch { cardInfo -> cardInfo.card == Card.Hearts6 && !cardInfo.selectable }
        }

        @Test
        fun `Card is not selectable because local player is disconnected`() {
            // Given
            setupDwitchGameInfo()
            assertThat(gameInfo.currentPlayerId).isEqualTo(localPlayerId) // Local player is the current player
            playersConnected[localPlayerId] = false // Local player is NOT connected

            // When
            val gameDashboardInfo = GameInfoFactory.createGameDashboardInfo(gameInfo, localPlayerId, playersConnected)
            val localPlayerCardsInHand = gameDashboardInfo.localPlayerDashboard.cardsInHand

            // Then

            // Club3 is not selectable because engine says it's not selectable (wrt game rules)
            assertThat(localPlayerCardsInHand).anyMatch { cardInfo -> cardInfo.card == Card.Clubs3 && !cardInfo.selectable }

            // Diamonds6 is not selectable because local player is not current player
            assertThat(localPlayerCardsInHand).anyMatch { cardInfo -> cardInfo.card == Card.Hearts6 && !cardInfo.selectable }
        }

        @Test
        fun `Local player waits for current player reconnection when it is disconnected`() {
            // Given
            val currentPlayerId = DwitchPlayerId(2)
            setupDwitchGameInfo(
                currentPlayerId = currentPlayerId
            )

            // current player is disconnected
            playersConnected[currentPlayerId] = false

            // local player is NOT the current player
            assertThat(gameInfo.currentPlayerId).isNotEqualTo(localPlayerId)

            // local player is connected
            playersConnected[localPlayerId] = true

            // When
            val gameDashboardInfo = GameInfoFactory.createGameDashboardInfo(gameInfo, localPlayerId, playersConnected)

            // Then
            assertThat(gameDashboardInfo.waitingForPlayerReconnection).isTrue
        }

        @Test
        fun `Local player does not wait for current player reconnection when it is disconnected when local player is disconnected`() {
            // Given
            val currentPlayerId = DwitchPlayerId(2)
            setupDwitchGameInfo(
                currentPlayerId = currentPlayerId
            )

            // current player is disconnected
            playersConnected[currentPlayerId] = false

            // local player is NOT the current player
            assertThat(gameInfo.currentPlayerId).isNotEqualTo(localPlayerId)

            // local player is disconnected
            playersConnected[localPlayerId] = false

            // When
            val gameDashboardInfo = GameInfoFactory.createGameDashboardInfo(gameInfo, localPlayerId, playersConnected)

            // Then
            assertThat(gameDashboardInfo.waitingForPlayerReconnection).isFalse
        }

        @Test
        fun `Local player does not wait for current player reconnection when it is disconnected if it is the current player`() {
            // Given
            setupDwitchGameInfo()

            // local player is the current player
            assertThat(gameInfo.currentPlayerId).isEqualTo(localPlayerId)

            // current/local player is disconnected
            playersConnected[localPlayerId] = false

            // When
            val gameDashboardInfo = GameInfoFactory.createGameDashboardInfo(gameInfo, localPlayerId, playersConnected)

            // Then
            assertThat(gameDashboardInfo.waitingForPlayerReconnection).isFalse

        }

        @Test
        fun `Local player can pass if it is the current player and it is connected`() {
            // Given
            setupDwitchGameInfo()
            assertThat(gameInfo.currentPlayerId).isEqualTo(localPlayerId) // Local player is the current player
            assertThat(playersConnected[localPlayerId]).isTrue // Local player is connected

            // When
            val gameDashboardInfo = GameInfoFactory.createGameDashboardInfo(gameInfo, localPlayerId, playersConnected)

            // Then
            assertThat(gameDashboardInfo.localPlayerDashboard.canPass).isTrue
        }

        @Test
        fun `Local player cannot pass if it is not the current player`() {
            // Given
            setupDwitchGameInfo(currentPlayerId = DwitchPlayerId(2))
            assertThat(gameInfo.currentPlayerId).isNotEqualTo(localPlayerId) // Local player is NOT the current player
            playersConnected[localPlayerId] = true // Local player is connected

            // When
            val gameDashboardInfo = GameInfoFactory.createGameDashboardInfo(gameInfo, localPlayerId, playersConnected)

            // Then
            assertThat(gameDashboardInfo.localPlayerDashboard.canPass).isFalse
        }

        @Test
        fun `Local player cannot pass if it is not connected`() {
            // Given
            setupDwitchGameInfo()
            assertThat(gameInfo.currentPlayerId).isEqualTo(localPlayerId) // Local player is the current player
            playersConnected[localPlayerId] = false // Local player is disconnected

            // When
            val gameDashboardInfo = GameInfoFactory.createGameDashboardInfo(gameInfo, localPlayerId, playersConnected)

            // Then
            assertThat(gameDashboardInfo.localPlayerDashboard.canPass).isFalse
        }
    }

    @Nested
    inner class CreateEndOfGameInfo {

        fun `New round can be started only when the game engine says so and only by the host`() {
            // Given
            val localPlayerIsHost = true
            setupDwitchGameInfo(
                gamePhase = DwitchGamePhase.RoundIsOver, // Irrelevant but set for consistency
                newRoundCanBeStarted = true
            )

            // When
            val endOfRoundInfo = GameInfoFactory.createEndOfGameInfo(gameInfo, localPlayerIsHost)

            // Then

            assertThat(endOfRoundInfo.canStartNewRound).isTrue
        }

        fun `New round cannot be started if the engine does not say so`() {
            // Given
            val localPlayerIsHost = true
            setupDwitchGameInfo(
                gamePhase = DwitchGamePhase.RoundIsOver, // Irrelevant but set for consistency
                newRoundCanBeStarted = false
            )

            // When
            val endOfRoundInfo = GameInfoFactory.createEndOfGameInfo(gameInfo, localPlayerIsHost)

            // Then
            assertThat(endOfRoundInfo.canStartNewRound).isFalse
        }

        fun `New round can be started by a guest`() {
            // Given
            val localPlayerIsHost = false // local player is a guest
            setupDwitchGameInfo(
                gamePhase = DwitchGamePhase.RoundIsOver, // Irrelevant but set for consistency
                newRoundCanBeStarted = true
            )

            // When
            val endOfRoundInfo = GameInfoFactory.createEndOfGameInfo(gameInfo, localPlayerIsHost)

            // Then

            assertThat(endOfRoundInfo.canStartNewRound).isFalse
        }
    }

    private fun setupDwitchGameInfo(
        currentPlayerId: DwitchPlayerId = localPlayerId,
        gamePhase: DwitchGamePhase = DwitchGamePhase.RoundIsOnGoing,
        newRoundCanBeStarted: Boolean = false
    ) {
        gameInfo = DwitchGameInfo(
            currentPlayerId = currentPlayerId,
            playerInfos = mapOf(
                DwitchPlayerId(1) to DwitchPlayerInfo(
                    id = DwitchPlayerId(1),
                    name = "Aragorn",
                    rank = DwitchRank.Asshole,
                    status = DwitchPlayerStatus.Playing,
                    dwitched = false,
                    cardsInHand = listOf(
                        DwitchCardInfo(Card.Clubs3, selectable = false),
                        DwitchCardInfo(Card.Hearts6, selectable = true)
                    ),
                    canPlay = true
                ),
                DwitchPlayerId(2) to DwitchPlayerInfo(
                    id = DwitchPlayerId(2),
                    name = "Gimli",
                    rank = DwitchRank.Neutral,
                    status = DwitchPlayerStatus.Playing,
                    dwitched = false,
                    cardsInHand = listOf(
                        DwitchCardInfo(Card.Hearts3, selectable = false),
                        DwitchCardInfo(Card.Diamonds6, selectable = true)
                    ),
                    canPlay = false
                ),
                DwitchPlayerId(3) to DwitchPlayerInfo(
                    id = DwitchPlayerId(3),
                    name = "Legolas",
                    rank = DwitchRank.President,
                    status = DwitchPlayerStatus.Playing,
                    dwitched = false,
                    cardsInHand = listOf(
                        DwitchCardInfo(Card.Diamonds3, selectable = false),
                        DwitchCardInfo(Card.Clubs6, selectable = true)
                    ),
                    canPlay = false
                )
            ),
            gamePhase = gamePhase,
            playingOrder = listOf(DwitchPlayerId(1), DwitchPlayerId(2), DwitchPlayerId(3)),
            joker = CardName.Two,
            lastCardPlayed = PlayedCards(Card.Clubs5),
            cardsOnTable = emptyList(),
            lastPlayerAction = null,
            newRoundCanBeStarted = newRoundCanBeStarted
        )
    }
}
