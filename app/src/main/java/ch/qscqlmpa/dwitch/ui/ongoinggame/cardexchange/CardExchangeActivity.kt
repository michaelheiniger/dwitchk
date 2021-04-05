package ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.tinylog.kotlin.Logger

class CardExchangeActivity : OngoingGameBaseActivity() {

    private lateinit var viewModel: CardExchangeViewModel

    private lateinit var playerRole: PlayerRole

    @ExperimentalFoundationApi
    @Composable
    private fun ActivityScreen() {
        val exchangeControlEnabled = viewModel.exchangeControlEnabled.observeAsState(false).value
        val cardsInHandItems = viewModel.cardsInHandItems.observeAsState(listOf()).value
        val cardsToExchangeItems = viewModel.cardsToExchangeItems.observeAsState(listOf()).value
        MaterialTheme {
            Surface(color = Color.White) {
                CardExchangeScreen(
                    cardsToExchange = cardsToExchangeItems,
                    cardsInHand = cardsInHandItems,
                    exchangeControlEnabled = exchangeControlEnabled,
                    onCardToExchangeClick = viewModel::removeCardFromExchange,
                    onCardInHandClick = viewModel::addCardToExchange,
                    onExchangeClick = viewModel::confirmExchange
                )
            }
        }
    }

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).getGameUiComponent()!!.inject(this)
        super.onCreate(savedInstanceState)

        playerRole = PlayerRole.valueOf(intent.getStringExtra(CommonExtraConstants.EXTRA_PLAYER_ROLE)!!)

        viewModel = ViewModelProvider(this, viewModelFactory).get(CardExchangeViewModel::class.java)

        viewModel.commands.observe(this) { command ->
            when (command) {
                CardExchangeCommand.Close -> {
                    GameRoomActivity.startActivity(this, playerRole)
                    finish()
                }
            }
        }
        setContent { ActivityScreen() }
    }

    companion object {
        fun startActivity(context: Context, playerRole: PlayerRole) {
            Logger.debug("Opening CardExchangeActivity...")
            val intent = Intent(context, CardExchangeActivity::class.java)
            intent.putExtra(CommonExtraConstants.EXTRA_PLAYER_ROLE, playerRole.name)
            context.startActivity(intent)
        }
    }
}