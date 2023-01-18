package ch.qscqlmpa.dwitchengine.actions.startnewgame

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchengine.rules.PlayingOrder

internal object GameBootstrap {

    fun createNewGame(
        playersInfo: List<DwitchPlayerOnboardingInfo>,
        initialGameSetup: InitialGameSetup
    ): DwitchGameState {
        val players = playersInfo.map { p ->
            val rank = initialGameSetup.getRankForPlayer(p.id)
            DwitchPlayer(
                p.id,
                p.name,
                initialGameSetup.getCardsForPlayer(p.id).toList(),
                rank,
                getPlayerState(rank),
                dwitched = false
            )
        }

        val currentPlayer = players.find { p -> p.status == DwitchPlayerStatus.Playing }!!
        val cardsInDeck = initialGameSetup.getRemainingCards()
        val activePlayers = players.map(DwitchPlayer::id).toSet()
        val joker = CardName.Two
        val cardGraveyard = emptyList<PlayedCards>()

        return DwitchGameState(
            DwitchGamePhase.RoundIsBeginning,
            players.associateBy { p -> p.id },
            PlayingOrder.getPlayingOrder(players),
            currentPlayer.id,
            activePlayers,
            emptyList(),
            emptyList(),
            joker,
            null,
            emptyList(),
            cardsInDeck,
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
