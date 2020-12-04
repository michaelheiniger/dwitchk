package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.ImageInfo
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.CardAdapter
import ch.qscqlmpa.dwitchengine.model.card.Card
import kotlinx.android.synthetic.main.player_dashboard_fragment.*
import timber.log.Timber


class CardExchangeFragment : OngoingGameBaseFragment(), CardAdapter.CardClickedListener {

    private lateinit var viewModel: CardExchangeViewModel

    override val layoutResource: Int = R.layout.card_exchange_fragment

    private lateinit var cardsInHandAdapter: CardAdapter

    override fun inject() {
        (activity!!.application as App).getGameUiComponent()!!.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardExchangeViewModel::class.java)

        startNewRoundBtn.setOnClickListener { viewModel.startNewRound() }
        pickBtn.setOnClickListener { viewModel.pickCard() }
        passBtn.setOnClickListener { viewModel.passTurn() }

        cardsInHandAdapter = CardAdapter(this)
        cardsInHandRw.layoutManager = GridLayoutManager(context, 4)
        cardsInHandRw.adapter = cardsInHandAdapter

        observeCardsInHand()
    }

    override fun onCardClicked(card: Card) {
//        if (canPlay) {
//            viewModel.playCard(card)
//        }
    }

    private fun observeCardsInHand() {
        viewModel.cardsInHand().observe(viewLifecycleOwner, { cards ->
                        cardsInHandAdapter.setData(cards.cardsInHands())
        })
    }

    companion object {
        fun create(): CardExchangeFragment {
            return CardExchangeFragment()
        }
    }
}
