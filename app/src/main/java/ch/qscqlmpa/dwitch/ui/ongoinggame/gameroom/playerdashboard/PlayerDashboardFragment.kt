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
import ch.qscqlmpa.dwitch.ui.ImageInfo
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment
import ch.qscqlmpa.dwitchengine.model.card.Card
import timber.log.Timber


class PlayerDashboardFragment : OngoingGameBaseFragment(), CardAdapter.CardClickedListener {

    override val layoutResource: Int = R.layout.player_dashboard_fragment

    private lateinit var viewModel: PlayerDashboardViewModel

    private lateinit var cardsInHandRw: RecyclerView
    private lateinit var cardsInHandAdapter: CardAdapter

    private lateinit var lastCardPlayedIv: ImageView
    private lateinit var gameInfoTv: TextView
    private lateinit var playersTv: TextView

    private lateinit var startNewRoundBtn: Button
    private lateinit var pickBtn: Button
    private lateinit var passBtn: Button

    private var canPlay: Boolean = false

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(PlayerDashboardViewModel::class.java)

        observeAndUpdateDashboard()
        observeCommands()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        playersTv = view.findViewById(R.id.playersTv) as TextView
        gameInfoTv = view.findViewById(R.id.gameInfoTv)

        startNewRoundBtn = view.findViewById(R.id.startNewRound) as Button
        startNewRoundBtn.setOnClickListener { viewModel.startNewRound() }

        pickBtn = view.findViewById(R.id.pickBtn) as Button
        pickBtn.setOnClickListener { viewModel.pickCard() }

        passBtn = view.findViewById(R.id.passBtn) as Button
        passBtn.setOnClickListener { viewModel.passTurn() }

        cardsInHandAdapter = CardAdapter(this)
        cardsInHandRw = view.findViewById(R.id.cardsInHandRw)
        cardsInHandRw.layoutManager = GridLayoutManager(context, 4)
        cardsInHandRw.adapter = cardsInHandAdapter

        lastCardPlayedIv = view.findViewById(R.id.lastCardIv)

        return view
    }

    override fun onCardClicked(card: Card) {
        if (canPlay) {
            viewModel.playCard(card)
        }
    }

    private fun observeCommands() {
        viewModel.commands().observe(this, { command ->
            when (command) {
                PlayerDashboardCommand.NavigateToHomeScreen -> MainActivity.start(activity!!)
            }
        })
    }

    private fun observeAndUpdateDashboard() {
        viewModel.playerDashboard().observe(this, { dashboard ->
            Timber.d("Dashboard update event: $dashboard")

            playersTv.text = dashboard.playersInfo()
            gameInfoTv.text = dashboard.gameInfo()

            startNewRoundBtn.isEnabled = dashboard.canStartNewRound()
            pickBtn.isEnabled = dashboard.canPickACard()
            passBtn.isEnabled = dashboard.canPass()
            canPlay = dashboard.canPlay()

            cardsInHandAdapter.setData(dashboard.cardsInHands())

            setImageView(lastCardPlayedIv, dashboard.lastCardPlayed())
        })
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
