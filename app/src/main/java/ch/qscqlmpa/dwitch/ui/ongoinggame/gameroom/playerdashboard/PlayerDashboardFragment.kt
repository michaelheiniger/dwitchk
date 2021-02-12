package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.databinding.FragmentPlayerDashboardBinding
import ch.qscqlmpa.dwitch.ui.ImageInfo
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange.CardExchangeFragment
import ch.qscqlmpa.dwitchengine.model.card.Card
import mu.KLogging

class PlayerDashboardFragment : OngoingGameBaseFragment(R.layout.fragment_player_dashboard), CardAdapter.CardClickedListener {

    private lateinit var viewModel: PlayerDashboardViewModel

    private lateinit var cardsInHandAdapter: CardAdapter

    private var canPlay: Boolean = false

    override fun inject() {
        (requireActivity().application as App).getGameUiComponent()!!.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPlayerDashboardBinding.bind(view)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PlayerDashboardViewModel::class.java)

        binding.startNewRoundBtn.setOnClickListener { viewModel.startNewRound() }
        binding.pickBtn.setOnClickListener { viewModel.pickCard() }
        binding.passBtn.setOnClickListener { viewModel.passTurn() }

        cardsInHandAdapter = CardAdapter(this)
        binding.cardsInHandRw.layoutManager = GridLayoutManager(context, 4)
        binding.cardsInHandRw.adapter = cardsInHandAdapter

        observeAndUpdateDashboard(binding)
        observeCommands()
    }

    override fun onCardClicked(card: Card) {
        if (canPlay) {
            viewModel.playCard(card)
        }
    }

    private fun observeAndUpdateDashboard(binding: FragmentPlayerDashboardBinding) {
        viewModel.playerDashboard().observe(
            viewLifecycleOwner,
            { dashboard ->
                logger.debug { "Dashboard update event: $dashboard" }

                binding.playersInfoTv.text = dashboard.playersInfo
                binding.gameInfoTv.text = dashboard.gameInfo
                binding.dwitchEventTv.text = dashboard.dwitchEvent

                binding.startNewRoundBtn.isEnabled = dashboard.canStartNewRound
                binding.pickBtn.isEnabled = dashboard.canPickACard
                binding.passBtn.isEnabled = dashboard.canPass
                canPlay = dashboard.canPlay

                cardsInHandAdapter.setData(dashboard.cardsInHands)

                setImageView(binding.lastCardPlayedIv, dashboard.lastCardPlayed)
            }
        )
    }

    private fun observeCommands() {
        viewModel.commands().observe(
            viewLifecycleOwner,
            { command ->
                logger.debug { "Command: $command" }
                when (command) {
                    is PlayerDashboardCommand.OpenCardExchange -> openCardExchange()
                    is PlayerDashboardCommand.OpenEndOfRound -> showEndOfRoundDialog(command.playersInfo)
                }
            }
        )
    }

    private fun openCardExchange() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.game_dashboard_fragment_container, CardExchangeFragment.create(), "card_exchange")
            .commit()
    }

    private fun showEndOfRoundDialog(playersInfo: List<PlayerEndOfRoundInfo>) {
        val dialog = EndOfRoundDialogFragment.newInstance(playersInfo)
        dialog.show(parentFragmentManager, "end_of_round_dialog")
    }

    private fun setImageView(imageView: ImageView, image: ImageInfo) {
        imageView.setImageResource(image.resourceId)
        imageView.contentDescription = image.description
    }

    companion object : KLogging() {
        fun create(): PlayerDashboardFragment {
            return PlayerDashboardFragment()
        }
    }
}
