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
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity

class CardExchangeActivity : OngoingGameBaseActivity() {

    private lateinit var viewModel: CardExchangeViewModel

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
                    onCardToExchangeClick = viewModel::addCardToExchange,
                    onCardInHandClick = viewModel::removeCardFromExchange,
                    onExchangeClick = viewModel::confirmExchange
                )
            }
        }
    }

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).getGameUiComponent()!!.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(CardExchangeViewModel::class.java)

        viewModel.commands.observe(this) { command ->
            when (command) {
                CardExchangeCommand.Close -> finish()
            }
        }

        setContent {
            ActivityScreen()
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, CardExchangeActivity::class.java))
        }
    }
}