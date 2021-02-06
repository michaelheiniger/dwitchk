package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.*

internal object TestEntityFactory {

    fun createHostPlayerInfo(): PlayerOnboardingInfo {
        return createHostPlayer().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer1Info(): PlayerOnboardingInfo {
        return createGuestPlayer1().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer2Info(): PlayerOnboardingInfo {
        return createGuestPlayer2().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer3Info(): PlayerOnboardingInfo {
        return createGuestPlayer3().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer4Info(): PlayerOnboardingInfo {
        return createGuestPlayer4().toPlayerOnboardingInfo()
    }

    fun createGuestPlayer5Info(): PlayerOnboardingInfo {
        return createGuestPlayer5().toPlayerOnboardingInfo()
    }

    fun createHostPlayer(
        cardsInHand: List<Card> = emptyList(),
        rank: Rank = Rank.Asshole,
        state: PlayerStatus = PlayerStatus.Playing,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): Player {
        return Player(
            id = PlayerDwitchId(100),
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
        rank: Rank = Rank.ViceAsshole,
        state: PlayerStatus = PlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): Player {
        return Player(
            id = PlayerDwitchId(101),
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
        rank: Rank = Rank.Neutral,
        state: PlayerStatus = PlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): Player {
        return Player(
            id = PlayerDwitchId(102),
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
        rank: Rank = Rank.Neutral,
        state: PlayerStatus = PlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): Player {
        return Player(
            id = PlayerDwitchId(103),
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
        rank: Rank = Rank.VicePresident,
        state: PlayerStatus = PlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): Player {
        return Player(
            id = PlayerDwitchId(104),
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
        rank: Rank = Rank.President,
        state: PlayerStatus = PlayerStatus.Waiting,
        dwitched: Boolean = false,
        hasPickedCard: Boolean = false
    ): Player {
        return Player(
            id = PlayerDwitchId(105),
            name = "Faramir",
            cardsInHand = cardsInHand,
            rank = rank,
            status = state,
            dwitched = dwitched,
            hasPickedACard = hasPickedCard
        )
    }

    fun createGameState(): GameState {
        val players = listOf(createHostPlayerInfo(), createGuestPlayer1Info(), createGuestPlayer2Info())
        return GameBootstrap.createNewGame(players, RandomInitialGameSetup(players.size))
    }
}
