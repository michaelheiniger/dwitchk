package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.CardResourceMapper
import ch.qscqlmpa.dwitch.ui.EntityTextMapper
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import timber.log.Timber


class PlayerDashboardFragment : OngoingGameBaseFragment(), CardAdapter.CardClickedListener {

    override val layoutResource: Int = R.layout.player_dashboard_fragment

    private lateinit var viewModel: PlayerDashboardViewModel

    private lateinit var cardsInHandRw: RecyclerView
    private lateinit var cardsInHandAdapter: CardAdapter

    private lateinit var lastCardPlayedIv: ImageView

    private lateinit var gameInfoTv: TextView

    private lateinit var playersTv: TextView

    private lateinit var startNewRound: Button
    private lateinit var pickBtn: Button
    private lateinit var passBtn: Button

    private var canPlay: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(PlayerDashboardViewModel::class.java)
        viewModel.playerDashboard().observe(this, { dashboard ->
            Timber.d("Dashboard update event: $dashboard")

            //TODO: Move to viewmodel ?
            val playerInfo = dashboard.playersInPlayingOrder
                .map { id -> dashboard.players.getValue(id) }
                .joinToString(" ") { player ->
                    "${player.name} (${getString(EntityTextMapper.rankText(player.rank))})"
                }

            playersTv.text = playerInfo

            startNewRound.isEnabled = dashboard.canStartNewRound
            pickBtn.isEnabled = dashboard.canPickACard
            passBtn.isEnabled = dashboard.canPass
            canPlay = dashboard.canPlay

            val cardInHandItems = dashboard.cardsInHand
                .map { card -> CardItem(card, isCardPlayable(card, dashboard)) }
            cardsInHandAdapter.setData(cardInHandItems)

            lastCardPlayedIv.setImageResource(CardResourceMapper.resourceForCard(dashboard.lastCardPlayed))
            lastCardPlayedIv.contentDescription = dashboard.lastCardPlayed.toString()

            gameInfoTv.text = when (dashboard.gamePhase) {
                GamePhase.RoundIsBeginning -> getString(R.string.round_is_beginning)
                GamePhase.RoundIsOnGoing -> ""
                GamePhase.RoundIsOver -> getString(R.string.round_is_over)
            }
        })

        viewModel.commands().observe(this, { command ->
            when (command) {
                PlayerDashboardCommand.NavigateToHomeScreen -> MainActivity.start(activity!!)
            }
        })
    }

    private fun isCardPlayable(card: Card, dashboard: PlayerDashboard): Boolean {
        Timber.v("Is card $card playable ? ${card.value()} >= ${dashboard.minimumCardValueAllowed.value} : ${card.value() >= dashboard.minimumCardValueAllowed.value}")
        return card.value() >= dashboard.minimumCardValueAllowed.value
                || card.name == dashboard.joker
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        playersTv = view.findViewById(R.id.playersTv) as TextView

        startNewRound = view.findViewById(R.id.startNewRound) as Button
        startNewRound.setOnClickListener { viewModel.startNewRound() }

        pickBtn = view.findViewById(R.id.pickBtn) as Button
        pickBtn.setOnClickListener { viewModel.pickCard() }

        passBtn = view.findViewById(R.id.passBtn) as Button
        passBtn.setOnClickListener { viewModel.passTurn() }

        cardsInHandAdapter = CardAdapter(this)
        cardsInHandRw = view.findViewById(R.id.cardsInHandRw)
        cardsInHandRw.layoutManager = GridLayoutManager(context, 4)
        cardsInHandRw.adapter = cardsInHandAdapter

        lastCardPlayedIv = view.findViewById(R.id.lastCardIv)

        gameInfoTv = view.findViewById(R.id.gameInfoTv)

        return view
    }

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    companion object {

        fun create(): PlayerDashboardFragment {
            return PlayerDashboardFragment()
        }
    }

    override fun onCardClicked(card: Card) {
        if (canPlay) {
            viewModel.playCard(card)
        }
    }
}
