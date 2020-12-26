package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

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
    private val gameInfo = gameInfoForDashboard.gameInfo
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

   private  fun cardsInHands(): List<CardItem> {
        return localPlayerInfo.cardsInHand.map { card -> CardItem(card, isCardPlayable(card)) }
    }

   private  fun lastCardPlayed(): ImageInfo {
        return ImageInfo(
            ResourceMapper.getResource(gameInfo.lastCardPlayed),
            gameInfo.lastCardPlayed.toString()
        )
    }

   private  fun playersInfo(): String {
        return gameInfo.playingOrder
            .map { id -> gameInfo.playerInfos.getValue(id) }
            .joinToString(" ") { player -> "${player.name} (${playerRank(player)})" }
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

    private fun cardHasValueHighEnough(card: Card) = card.value() >= localPlayerInfo.minimumPlayingCardValueAllowed.value || cardIsJoker(card)

    private fun cardIsJoker(card: Card) = card.name == gameInfo.joker
}