package ch.qscqlmpa.dwitch.ui.home.main


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitchmodel.game.ResumableGameInfo
import java.util.*

internal class ExistingGameAdapter(
    private val listener: ExistingGameClickedListener
) : RecyclerView.Adapter<ExistingGameAdapter.GameViewHolder>() {

    private val data = ArrayList<ResumableGameInfo>()

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

    fun setData(gameList: List<ResumableGameInfo>?) {
        data.clear()
        if (gameList != null) {
            data.addAll(gameList)
        }
        notifyDataSetChanged()
    }

    internal interface ExistingGameClickedListener {

        fun onGameClicked(selectedGame: ResumableGameInfo)
    }

    internal class GameViewHolder(itemView: View, listener: ExistingGameClickedListener) : RecyclerView.ViewHolder(itemView) {

        private lateinit var resumableGameInfo: ResumableGameInfo

        private var gameNameTv: TextView = itemView.findViewById(R.id.gameNameTv)

        init {
            itemView.setOnClickListener {
                listener.onGameClicked(resumableGameInfo)
            }
        }

        fun bind(resumableGameInfo: ResumableGameInfo) {
            this.resumableGameInfo = resumableGameInfo
            val playersName = resumableGameInfo.playersName.joinToString(separator = ", ")
            gameNameTv.text = String.format("%s - %s", resumableGameInfo.name, playersName)
        }
    }
}
