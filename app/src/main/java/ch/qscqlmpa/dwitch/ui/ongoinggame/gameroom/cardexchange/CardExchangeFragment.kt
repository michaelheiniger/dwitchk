package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseDialogFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.CardAdapter
import ch.qscqlmpa.dwitchengine.model.card.Card
import kotlinx.android.synthetic.main.card_exchange_fragment.*


class CardExchangeFragment : OngoingGameBaseDialogFragment() {

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

        exchangeBtn.setOnClickListener { viewModel.confirmChoice() }

        setupCardsInHand()
        setupCardsChosen()
    }

    private fun setupCardsInHand() {
        cardsChosenAdapter = CardAdapter(CardsInHandClickedListener(viewModel))
        cardsChosenRw.layoutManager = GridLayoutManager(context, 4)
        cardsChosenRw.adapter = cardsChosenAdapter

        viewModel.cardsInHand().observe(viewLifecycleOwner, { cardItems ->
            cardsInHandAdapter.setData(cardItems)
        })
    }

    private fun setupCardsChosen() {
        cardsInHandAdapter = CardAdapter(CardsChoseClickedListener(viewModel))
        cardsInHandRw.layoutManager = GridLayoutManager(context, 4)
        cardsInHandRw.adapter = cardsInHandAdapter

        viewModel.cardsChosen().observe(viewLifecycleOwner, { cardItems ->
            cardsChosenAdapter.setData(cardItems)
        })
    }

    private class CardsInHandClickedListener(private val viewModel: CardExchangeViewModel): CardAdapter.CardClickedListener  {
        override fun onCardClicked(card: Card) {
            viewModel.cardInHandClicked(card)
        }
    }

    private class CardsChoseClickedListener(private val viewModel: CardExchangeViewModel): CardAdapter.CardClickedListener  {
        override fun onCardClicked(card: Card) {
            viewModel.cardChosenClicked(card)
        }
    }

    companion object {
        fun create(): CardExchangeFragment {
            return CardExchangeFragment()
        }
    }
}
