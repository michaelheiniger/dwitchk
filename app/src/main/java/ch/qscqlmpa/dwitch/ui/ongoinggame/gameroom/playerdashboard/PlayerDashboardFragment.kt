package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.ImageInfo
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange.CardExchangeFragment
import ch.qscqlmpa.dwitchengine.model.card.Card
import kotlinx.android.synthetic.main.player_dashboard_fragment.*
import timber.log.Timber


class PlayerDashboardFragment : OngoingGameBaseFragment(), CardAdapter.CardClickedListener {

    private lateinit var viewModel: PlayerDashboardViewModel

    override val layoutResource: Int = R.layout.player_dashboard_fragment

    private lateinit var cardsInHandAdapter: CardAdapter

    private var canPlay: Boolean = false

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PlayerDashboardViewModel::class.java)

        startNewRoundBtn.setOnClickListener { viewModel.startNewRound() }
        pickBtn.setOnClickListener { viewModel.pickCard() }
        passBtn.setOnClickListener { viewModel.passTurn() }

        cardsInHandAdapter = CardAdapter(this)
        cardsInHandRw.layoutManager = GridLayoutManager(context, 4)
        cardsInHandRw.adapter = cardsInHandAdapter

        observeAndUpdateDashboard()
        observeCommands()
    }

    override fun onCardClicked(card: Card) {
        if (canPlay) {
            viewModel.playCard(card)
        }
    }

    private fun observeAndUpdateDashboard() {
        viewModel.playerDashboard().observe(viewLifecycleOwner, { dashboard ->
            Timber.d("Dashboard update event: $dashboard")

            playersInfoTv.text = dashboard.playersInfo
            gameInfoTv.text = dashboard.gameInfo
            dwitchEventTv.text = dashboard.dwitchEvent

            startNewRoundBtn.isEnabled = dashboard.canStartNewRound
            pickBtn.isEnabled = dashboard.canPickACard
            passBtn.isEnabled = dashboard.canPass
            canPlay = dashboard.canPlay

            cardsInHandAdapter.setData(dashboard.cardsInHands)

            setImageView(lastCardPlayedIv, dashboard.lastCardPlayed)
        })
    }

    private fun observeCommands() {
        viewModel.commands().observe(viewLifecycleOwner, { command ->
            Timber.d("Command: $command")
            when (command) {
                is PlayerDashboardCommand.OpenCardExchange -> openCardExchange()
            }
        })
    }

    private fun openCardExchange() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.game_dashboard_fragment_container, CardExchangeFragment.create(), "card_exchange")
            .commit()
//        supportFragmentManager.findFragmentByTag("card_exchange")
//        ft.addToBackStack(null)
//        CardExchangeFragment.create().show(ft, "dialog")
    }

    private fun setImageView(imageView: ImageView, image: ImageInfo) {
        imageView.setImageResource(image.resourceId)
        imageView.contentDescription = image.description
    }

    companion object {
        fun create(): PlayerDashboardFragment {
            return PlayerDashboardFragment()
        }
    }
}
