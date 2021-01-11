package ch.qscqlmpa.dwitchengine.actions.startnewgame

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerOnboardingInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchengine.rules.PlayingOrder

internal object GameBootstrap {

    fun createNewGame(playersInfo: List<PlayerOnboardingInfo>, initialGameSetup: InitialGameSetup): GameState {
        val players = playersInfo.mapIndexed { index, p ->
            val rank = initialGameSetup.getRankForPlayer(index)
            Player(
                p.id,
                p.name,
                initialGameSetup.getCardsForPlayer(index),
                rank,
                getPlayerState(rank),
                dwitched = false,
                hasPickedACard = false
            )
        }

        val currentPlayer = players.find { p -> p.status == PlayerStatus.Playing }!!
        val cardsInDeck = initialGameSetup.getRemainingCards().toMutableList()
        val firstCardOnTable = cardsInDeck.removeAt(0)
        val activePlayers = players.map(Player::id).toSet()
        val joker = CardName.Two
        val cardsOnTable = listOf(firstCardOnTable)
        val cardGraveyard = emptyList<Card>()

        return GameState(
            GamePhase.RoundIsBeginning,
            players.map { p -> p.id to p }.toMap(),
            PlayingOrder.getPlayingOrder(players),
            currentPlayer.id,
            activePlayers,
            emptyList(),
            emptyList(),
            joker,
            null,
            cardsOnTable,
            cardsInDeck.toList(),
            cardGraveyard,
        )
    }

    private fun getPlayerState(rank: Rank): PlayerStatus {
        return when (rank) {
            is Rank.President, is Rank.VicePresident, is Rank.Neutral, is Rank.ViceAsshole -> PlayerStatus.Waiting
            is Rank.Asshole -> PlayerStatus.Playing
        }
    }
}