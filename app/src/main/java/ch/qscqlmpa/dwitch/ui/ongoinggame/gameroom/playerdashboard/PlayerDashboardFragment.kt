package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import timber.log.Timber


class PlayerDashboardFragment : OngoingGameBaseFragment(), CardAdapter.CardClickedListener {

    override val layoutResource: Int = R.layout.player_dashboard_fragment

    private lateinit var viewModel: PlayerDashboardViewModel

    private lateinit var cardsInHandRw: RecyclerView
    private lateinit var cardsInHandAdapter: CardAdapter

    private lateinit var lastCardPlayedIv: ImageView

    private lateinit var lastGameEventTv: TextView

    private lateinit var playersTv: TextView

    private lateinit var pickBtn: Button
    private lateinit var passBtn: Button

    private var canPlay: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PlayerDashboardViewModel::class.java)
        viewModel.playerDashboard().observe(this, Observer { dashboard ->
            Timber.d("Dashboard update event: $dashboard")

            //FIXME
//            val playerInfo = dashboard.playersInPlayingOrder
//                    .map { id -> dashboard.players.getValue(id) }
//                    .map { player -> "${player.name} (${getString(player.rank.)})" }
//                    .joinToString(" ")

//            playersTv.text = playerInfo

//            val string = SpannableString("Text with strikethrough span")
//            string.setSpan(StrikethroughSpan(), 10, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            pickBtn.isEnabled = dashboard.canPickACard
            passBtn.isEnabled = dashboard.canPass
            canPlay = dashboard.canPlay

            val cardInHandItems = dashboard.cardsInHand
                    .map { card -> CardItem(card, isCardPlayable(card, dashboard)) }
            cardsInHandAdapter.setData(cardInHandItems)

            //FIXME
//            lastCardPlayedIv.setImageResource(dashboard.lastCardPlayed.resourceId.id)
//            lastCardPlayedIv.contentDescription = dashboard.lastCardPlayed.resourceId.id.toString()

            //FIXME
//            lastGameEventTv.text = when (dashboard.gameEvent) {
//                is GameEvent.TableHasBeenCleared -> "Table cleared with card ${dashboard.gameEvent.lastCardPlayed.name.description.id}" //TODO translation
//                is GameEvent.TableHasBeenClearedTurnPassed -> "Table cleared because no one else could play" //TODO translation
//                null -> ""
//            }
        })
    }

    private fun isCardPlayable(card: Card, dashboard: PlayerDashboard): Boolean {
        return card.value() >= dashboard.minimumCardValueAllowed.value
                || card.name == dashboard.joker
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        playersTv = view.findViewById(R.id.playersTv) as TextView

        pickBtn = view.findViewById(R.id.pickBtn) as Button
        pickBtn.setOnClickListener { viewModel.pickCard() }

        passBtn = view.findViewById(R.id.passBtn) as Button
        passBtn.setOnClickListener { viewModel.passTurn() }

        cardsInHandAdapter = CardAdapter(this)
        cardsInHandRw = view.findViewById(R.id.cardsInHandRw)
        cardsInHandRw.layoutManager = GridLayoutManager(context, 4)
        cardsInHandRw.adapter = cardsInHandAdapter

        lastCardPlayedIv = view.findViewById(R.id.lastCardIv)

        lastGameEventTv = view.findViewById(R.id.gameEventTv)

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
