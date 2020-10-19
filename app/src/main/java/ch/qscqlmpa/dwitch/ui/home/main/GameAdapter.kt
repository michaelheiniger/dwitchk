package ch.qscqlmpa.dwitch.ui.home.main


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import java.util.*

internal class GameAdapter(private val listener: GameClickedListener) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

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

    internal interface GameClickedListener {

        fun onGameClicked(advertisedGame: AdvertisedGame)
    }

    internal class GameViewHolder(itemView: View, listener: GameClickedListener) : RecyclerView.ViewHolder(itemView) {

        private lateinit var advertisedGame: AdvertisedGame

        private var gameNameTv: TextView = itemView.findViewById(R.id.gameNameTv)

        init {
            itemView.setOnClickListener {
                listener.onGameClicked(advertisedGame)
            }
        }

        fun bind(advertisedGame: AdvertisedGame) {
            this.advertisedGame = advertisedGame
            gameNameTv.text = String.format("%s (%s) at %s", advertisedGame.name, advertisedGame.ipAddress, advertisedGame.discoveryTimeAsString())
        }
    }
}
