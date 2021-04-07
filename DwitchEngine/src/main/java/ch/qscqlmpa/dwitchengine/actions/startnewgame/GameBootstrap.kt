package ch.qscqlmpa.dwitchengine.actions.startnewgame

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchengine.rules.PlayingOrder

internal object GameBootstrap {

    fun createNewGame(playersInfo: List<DwitchPlayerOnboardingInfo>, initialGameSetup: InitialGameSetup): DwitchGameState {
        val players = playersInfo.mapIndexed { index, p ->
            val rank = initialGameSetup.getRankForPlayer(index)
            DwitchPlayer(
                p.id,
                p.name,
                initialGameSetup.getCardsForPlayer(index),
                rank,
                getPlayerState(rank),
                dwitched = false,
                hasPickedACard = false
            )
        }

        val currentPlayer = players.find { p -> p.status == DwitchPlayerStatus.Playing }!!
        val cardsInDeck = initialGameSetup.getRemainingCards().toMutableList()
        val firstCardOnTable = cardsInDeck.removeAt(0)
        val activePlayers = players.map(DwitchPlayer::id).toSet()
        val joker = CardName.Two
        val cardsOnTable = listOf(firstCardOnTable)
        val cardGraveyard = emptyList<Card>()

        return DwitchGameState(
            DwitchGamePhase.RoundIsBeginning,
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

    private fun getPlayerState(rank: DwitchRank): DwitchPlayerStatus {
        return when (rank) {
            is DwitchRank.President, is DwitchRank.VicePresident, is DwitchRank.Neutral, is DwitchRank.ViceAsshole -> DwitchPlayerStatus.Waiting
            is DwitchRank.Asshole -> DwitchPlayerStatus.Playing
        }
    }
}
