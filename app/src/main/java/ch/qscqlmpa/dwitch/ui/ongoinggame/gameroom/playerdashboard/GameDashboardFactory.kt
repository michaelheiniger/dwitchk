package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ImageInfo
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitch.ui.utils.TextProvider
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.info.PlayerInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameInfoForDashboard
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

class GameDashboardFactory(
    gameInfoForDashboard: GameInfoForDashboard,
    connectionState: PlayerConnectionState,
    private val textProvider: TextProvider
) {

    private val dashboardEnabled = connectionState == PlayerConnectionState.CONNECTED
    private val gameInfo = gameInfoForDashboard.getGameInfo()
    private val localPlayerInfo = gameInfoForDashboard.localPlayerInfo

    fun create(): GameDashboard {
        return GameDashboard(
            canStartNewRound(),
            canPickACard(),
            canPass(),
            canPlay(),
            cardsInHands(),
            lastCardPlayed(),
            playersInfo(),
            gameInfo()
        )
    }

    private fun canStartNewRound(): Boolean {
        return dashboardEnabled && localPlayerInfo.canStartNewRound
    }

    private fun canPickACard(): Boolean {
        return dashboardEnabled && localPlayerInfo.canPickACard
    }

    private fun canPass(): Boolean {
        return dashboardEnabled && localPlayerInfo.canPass
    }

    private fun canPlay(): Boolean {
        return dashboardEnabled && localPlayerInfo.canPlay
    }

    private fun cardsInHands(): List<CardItem> {
        return localPlayerInfo.cardsInHand.map { card -> CardItem(card, isCardPlayable(card)) }
    }

    private fun lastCardPlayed(): ImageInfo {
        return ImageInfo(ResourceMapper.getResource(gameInfo.lastCardPlayed), gameInfo.lastCardPlayed.toString())
    }

    private fun playersInfo(): CharSequence {
        val text = gameInfo.playingOrder
            .map { id -> gameInfo.playerInfos.getValue(id) }
            .map { player -> "${player.name} (${playerRank(player)})" }

        val indexOfCurrentPlayer = gameInfo.playingOrder.indexOf(gameInfo.currentPlayerId)
        val startIndex = text
            .take(indexOfCurrentPlayer)
            .map { s -> s.length + 1 }
            .sum()

        val endIndex = text
            .take(indexOfCurrentPlayer + 1)
            .mapIndexed { index, s ->
                if (index < text.size - 1) {
                    s.length + 1
                }
                else {
                    s.length
                }
            }
            .sum()

        gameInfo.getCurrentPlayer().name.length + 3 + playerRank(gameInfo.getCurrentPlayer()).length

        val sb = SpannableStringBuilder(text.joinToString(" "))
        val bss = StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sb.setSpan(bss, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make first 4 characters Bold


        return sb
    }

    private fun gameInfo(): String {
        return when (gameInfo.gamePhase) {
            GamePhase.RoundIsBeginningWithCardExchange -> textProvider.getText(R.string.round_is_beginning_with_card_exchange)
            GamePhase.RoundIsBeginning -> textProvider.getText(R.string.round_is_beginning)
            GamePhase.RoundIsOnGoing -> ""
            GamePhase.RoundIsOver -> textProvider.getText(R.string.round_is_over)
        }
    }

    private fun playerRank(player: PlayerInfo) = textProvider.getText(ResourceMapper.getResource(player.rank))

    private fun isCardPlayable(card: Card) = cardHasValueHighEnough(card) || cardIsJoker(card)

    private fun cardHasValueHighEnough(card: Card) =
        card.value() >= localPlayerInfo.minimumPlayingCardValueAllowed.value || cardIsJoker(card)

    private fun cardIsJoker(card: Card) = card.name == gameInfo.joker
}