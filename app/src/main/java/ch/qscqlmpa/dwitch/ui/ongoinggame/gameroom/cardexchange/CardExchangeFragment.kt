package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.CardAdapter
import ch.qscqlmpa.dwitchengine.model.card.Card
import kotlinx.android.synthetic.main.card_exchange_fragment.*
import mu.KLogging

class CardExchangeFragment : OngoingGameBaseFragment() {

    private lateinit var viewModel: CardExchangeViewModel

    override val layoutResource: Int = R.layout.card_exchange_fragment

    private lateinit var cardsChosenAdapter: CardAdapter

    private lateinit var cardsInHandAdapter: CardAdapter

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardExchangeViewModel::class.java)

        viewModel.commands().observe(
            viewLifecycleOwner,
            { command ->
                logger.debug { "Command: $command" }
                when (command) {
                    CardExchangeCommand.Close -> {
                        val supportFragmentManager = requireActivity().supportFragmentManager
                        supportFragmentManager.popBackStack()
                    }
                }
            }
        )

        viewModel.submitControl().observe(viewLifecycleOwner, { model -> exchangeBtn.isEnabled = model.enabled })
        exchangeBtn.setOnClickListener { viewModel.confirmChoice() }

        setupCardsChosen()
        setupCardsInHand()
    }

    private fun setupCardsChosen() {
        cardsChosenAdapter = CardAdapter(CardsChosenClickedListener(viewModel))
        cardsChosenRw.layoutManager = GridLayoutManager(context, 4)
        cardsChosenRw.adapter = cardsChosenAdapter

        viewModel.cardsChosen().observe(viewLifecycleOwner, { cardItems -> cardsChosenAdapter.setData(cardItems) })
    }

    private fun setupCardsInHand() {
        cardsInHandAdapter = CardAdapter(CardsInHandClickedListener(viewModel))
        cardsInHandRw.layoutManager = GridLayoutManager(context, 4)
        cardsInHandRw.adapter = cardsInHandAdapter

        viewModel.cardsInHand().observe(
            viewLifecycleOwner,
            { cardItems ->
                logger.debug { "cardsInHandAdapter updated: $cardItems" }
                cardsInHandAdapter.setData(cardItems)
            }
        )
    }

    private class CardsInHandClickedListener(private val viewModel: CardExchangeViewModel) : CardAdapter.CardClickedListener {
        override fun onCardClicked(card: Card) {
            viewModel.cardInHandClicked(card)
        }
    }

    private class CardsChosenClickedListener(private val viewModel: CardExchangeViewModel) : CardAdapter.CardClickedListener {
        override fun onCardClicked(card: Card) {
            viewModel.cardChosenClicked(card)
        }
    }

    companion object : KLogging() {
        fun create(): CardExchangeFragment {
            return CardExchangeFragment()
        }
    }
}
