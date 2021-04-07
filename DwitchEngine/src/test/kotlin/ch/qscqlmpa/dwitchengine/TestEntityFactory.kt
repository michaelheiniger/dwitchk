package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.*

internal object TestEntityFactory {

    fun createHostPlayerInfo(): DwitchPlayerOnboardingInfo {
        return createHostPlayer().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer1Info(): DwitchPlayerOnboardingInfo {
        return createGuestPlayer1().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer2Info(): DwitchPlayerOnboardingInfo {
        return createGuestPlayer2().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer3Info(): DwitchPlayerOnboardingInfo {
        return createGuestPlayer3().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer4Info(): DwitchPlayerOnboardingInfo {
        return createGuestPlayer4().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer5Info(): DwitchPlayerOnboardingInfo {
        return createGuestPlayer5().toPlayerOnboardingInfo()
    }

    fun createHostPlayer(
        cardsInHand: List<Card> = emptyList(),
        rank: DwitchRank = DwitchRank.Asshole,
        state: DwitchPlayerStatus = DwitchPlayerStatus.Playing,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): DwitchPlayer {
        return DwitchPlayer(
            id = DwitchPlayerId(100),
            name = "Aragorn",
            cardsInHand = cardsInHand,
            rank = rank,
            status = state,
            dwitched = dwitched,
            hasPickedACard = hasPickedCard
        )
    }

    fun createGuestPlayer1(
        cardsInHand: List<Card> = emptyList(),
        rank: DwitchRank = DwitchRank.ViceAsshole,
        state: DwitchPlayerStatus = DwitchPlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): DwitchPlayer {
        return DwitchPlayer(
            id = DwitchPlayerId(101),
            name = "Boromir",
            cardsInHand = cardsInHand,
            rank = rank,
            status = state,
            dwitched = dwitched,
            hasPickedACard = hasPickedCard
        )
    }

    fun createGuestPlayer2(
        cardsInHand: List<Card> = emptyList(),
        rank: DwitchRank = DwitchRank.Neutral,
        state: DwitchPlayerStatus = DwitchPlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): DwitchPlayer {
        return DwitchPlayer(
            id = DwitchPlayerId(102),
            name = "Celeborn",
            cardsInHand = cardsInHand,
            rank = rank,
            status = state,
            dwitched = dwitched,
            hasPickedACard = hasPickedCard
        )
    }

    fun createGuestPlayer3(
        cardsInHand: List<Card> = emptyList(),
        rank: DwitchRank = DwitchRank.Neutral,
        state: DwitchPlayerStatus = DwitchPlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): DwitchPlayer {
        return DwitchPlayer(
            id = DwitchPlayerId(103),
            name = "Denethor",
            cardsInHand = cardsInHand,
            rank = rank,
            status = state,
            dwitched = dwitched,
            hasPickedACard = hasPickedCard
        )
    }

    fun createGuestPlayer4(
        cardsInHand: List<Card> = emptyList(),
        rank: DwitchRank = DwitchRank.VicePresident,
        state: DwitchPlayerStatus = DwitchPlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): DwitchPlayer {
        return DwitchPlayer(
            id = DwitchPlayerId(104),
            name = "Eowin",
            cardsInHand = cardsInHand,
            rank = rank,
            status = state,
            dwitched = dwitched,
            hasPickedACard = hasPickedCard
        )
    }

    fun createGuestPlayer5(
        cardsInHand: List<Card> = emptyList(),
        rank: DwitchRank = DwitchRank.President,
        state: DwitchPlayerStatus = DwitchPlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): DwitchPlayer {
        return DwitchPlayer(
            id = DwitchPlayerId(105),
            name = "Faramir",
            cardsInHand = cardsInHand,
            rank = rank,
            status = state,
            dwitched = dwitched,
            hasPickedACard = hasPickedCard
        )
    }

    fun createGameState(): DwitchGameState {
        val players = listOf(createHostPlayerInfo(), createGuestPlayer1Info(), createGuestPlayer2Info())
        return GameBootstrap.createNewGame(players, RandomInitialGameSetup(players.size))
    }
}
