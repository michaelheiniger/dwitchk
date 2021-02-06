package ch.qscqlmpa.dwitch.ui.home.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import java.util.*

internal class AdvertisedGameAdapter(private val listener: AdvertisedGameClickedListener) : RecyclerView.Adapter<AdvertisedGameAdapter.GameViewHolder>() {

    private val data = ArrayList<AdvertisedGame>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return GameViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(gameList: List<AdvertisedGame>?) {
        data.clear()
        if (gameList != null) {
            data.addAll(gameList)
        }
        notifyDataSetChanged()
    }

    internal interface AdvertisedGameClickedListener {

        fun onGameClicked(selectedGame: AdvertisedGame)
    }

    internal class GameViewHolder(itemView: View, listener: AdvertisedGameClickedListener) : RecyclerView.ViewHolder(itemView) {

        private lateinit var advertisedGame: AdvertisedGame

        private var gameNameTv: TextView = itemView.findViewById(R.id.gameNameTv)

        init {
            itemView.setOnClickListener {
                listener.onGameClicked(advertisedGame)
            }
        }

        fun bind(advertisedGame: AdvertisedGame) {
            this.advertisedGame = advertisedGame
            gameNameTv.text = String.format("%s (%s) at %s", advertisedGame.gameName, advertisedGame.gameIpAddress, advertisedGame.discoveryTimeAsString())
        }
    }
}
