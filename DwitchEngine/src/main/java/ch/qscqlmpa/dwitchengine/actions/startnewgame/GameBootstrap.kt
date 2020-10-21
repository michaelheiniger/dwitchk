package ch.qscqlmpa.dwitchengine.actions.startnewgame

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.*
import ch.qscqlmpa.dwitchengine.rules.PlayingOrder

internal object GameBootstrap {

    fun createNewGame(playersInfo: List<PlayerInfo>, localPlayerId: PlayerInGameId, initialGameSetup: InitialGameSetup): GameState {
        val players = playersInfo.mapIndexed { index, p ->
            val rank = initialGameSetup.getRankForPlayer(index)
            Player(
                    p.id,
                    p.name,
                    initialGameSetup.getCardsForPlayer(index),
                    rank,
                    getPlayerState(rank),
                    dwitched = false,
                    hasPickedCard = false
            )
        }

        val currentPlayer = players.find { p -> p.state == PlayerState.Playing }!!
        val cardsInDeck = initialGameSetup.getRemainingCards().toMutableList()
        val firstCardOnTable = cardsInDeck.removeAt(0)
        val activePlayers = players.map(Player::inGameId).toSet()
        val joker = CardName.Two
        val cardsOnTable = listOf(firstCardOnTable)
        val cardGraveyard = emptyList<Card>()

        return GameState(
                GamePhase.RoundIsBeginning,
                players.map { p -> p.inGameId to p }.toMap(),
                PlayingOrder.getPlayingOrder(players),
                localPlayerId,
                currentPlayer.inGameId,
                activePlayers,
                emptyList(),
                joker,
                null,
                cardsOnTable,
                cardsInDeck.toList(),
                cardGraveyard

        )
    }

    private fun getPlayerState(rank: Rank): PlayerState {
        return when (rank) {
            is Rank.President, is Rank.VicePresident, is Rank.Neutral, is Rank.ViceAsshole -> PlayerState.Waiting
            is Rank.Asshole -> PlayerState.Playing
        }
    }
}