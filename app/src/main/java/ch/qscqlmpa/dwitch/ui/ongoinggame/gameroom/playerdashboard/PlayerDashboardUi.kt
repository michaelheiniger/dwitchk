package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.CardResourceMapper
import ch.qscqlmpa.dwitch.ui.EntityTextMapper
import ch.qscqlmpa.dwitch.ui.ImageInfo
import ch.qscqlmpa.dwitch.ui.utils.TextProvider
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import timber.log.Timber

class PlayerDashboardUi(private val dashboard: PlayerDashboard, private val textProvider: TextProvider) {

    fun canStartNewRound(): Boolean {
        return dashboard.canStartNewRound
    }

    fun canPickACard(): Boolean {
        return dashboard.canPickACard
    }

    fun canPass(): Boolean {
        return dashboard.canPass
    }

    fun canPlay(): Boolean {
        return dashboard.canPlay
    }

    fun cardsInHands(): List<CardItem> {
        return dashboard.cardsInHand
            .map { card -> CardItem(card, isCardPlayable(card, dashboard)) }
    }

    fun lastCardPlayed(): ImageInfo {
        return ImageInfo(
            CardResourceMapper.resourceForCard(dashboard.lastCardPlayed),
            dashboard.lastCardPlayed.toString()
        )
    }

    fun playersInfo(): String {
        return dashboard.playersInPlayingOrder
            .map { id -> dashboard.players.getValue(id) }
            .joinToString(" ") { player -> "${player.name} (${playerRank(player)})" }
    }

    fun gameInfo(): String {
        return when (dashboard.gamePhase) {
            GamePhase.RoundIsBeginning -> textProvider.getText(R.string.round_is_beginning)
            GamePhase.RoundIsOnGoing -> ""
            GamePhase.RoundIsOver -> textProvider.getText(R.string.round_is_over)
        }
    }

    private fun playerRank(player: Player) = textProvider.getText(EntityTextMapper.rankText(player.rank))

    private fun isCardPlayable(card: Card, dashboard: PlayerDashboard): Boolean {
        Timber.v("Is card $card playable ? ${card.value()} >= ${dashboard.minimumCardValueAllowed.value} : ${card.value() >= dashboard.minimumCardValueAllowed.value}")
        return card.value() >= dashboard.minimumCardValueAllowed.value
                || card.name == dashboard.joker
    }
}