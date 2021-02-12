package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.databinding.FragmentCardExchangeBinding
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.CardAdapter
import ch.qscqlmpa.dwitchengine.model.card.Card
import mu.KLogging

class CardExchangeFragment : OngoingGameBaseFragment(R.layout.fragment_card_exchange) {

    private lateinit var viewModel: CardExchangeViewModel

    private lateinit var cardsChosenAdapter: CardAdapter

    private lateinit var cardsInHandAdapter: CardAdapter

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCardExchangeBinding.bind(view)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CardExchangeViewModel::class.java)

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

        viewModel.submitControl().observe(viewLifecycleOwner, { model -> binding.exchangeBtn.isEnabled = model.enabled })
        binding.exchangeBtn.setOnClickListener { viewModel.confirmChoice() }

        setupCardsChosen(binding)
        setupCardsInHand(binding)
    }

    private fun setupCardsChosen(binding: FragmentCardExchangeBinding) {
        cardsChosenAdapter = CardAdapter(CardsChosenClickedListener(viewModel))
        binding.cardsChosenRw.layoutManager = GridLayoutManager(context, 4)
        binding.cardsChosenRw.adapter = cardsChosenAdapter

        viewModel.cardsChosen().observe(viewLifecycleOwner, { cardItems -> cardsChosenAdapter.setData(cardItems) })
    }

    private fun setupCardsInHand(binding: FragmentCardExchangeBinding) {
        cardsInHandAdapter = CardAdapter(CardsInHandClickedListener(viewModel))
        binding.cardsInHandRw.layoutManager = GridLayoutManager(context, 4)
        binding.cardsInHandRw.adapter = cardsInHandAdapter

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
