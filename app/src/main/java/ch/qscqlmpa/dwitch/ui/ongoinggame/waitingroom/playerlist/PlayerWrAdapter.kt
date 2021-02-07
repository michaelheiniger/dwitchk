package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.playerlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.PlayerConnectionStateTextMapper
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.playerlist.PlayerWrAdapter.PlayerViewHolder
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import java.util.*

internal class PlayerWrAdapter : RecyclerView.Adapter<PlayerViewHolder>() {

    private val data: MutableList<PlayerWr> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.player_wr_item, parent, false)
        return PlayerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemId(position: Int): Long {
        return data[position].dwitchId.value
    }

    fun setData(playerList: List<PlayerWr>) {
        val diffResult = DiffUtil.calculateDiff(PlayerDiffCallback(data, playerList))
        data.clear()
        data.addAll(playerList)
        diffResult.dispatchUpdatesTo(this)
    }

    internal class PlayerViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        private var playerNameTv: TextView = itemView.findViewById(R.id.playerNameTv)
        private var playerReadyCkb: CheckBox = itemView.findViewById(R.id.playerReadyCkb)
        private var connectionStateTv: TextView = itemView.findViewById(R.id.connectionStateTv)

        fun bind(player: PlayerWr) {
            playerNameTv.text = player.name
            playerReadyCkb.isChecked = player.ready
            setReadyCheckboxText()
            connectionStateTv.text = view.context.getString(
                PlayerConnectionStateTextMapper.resource(player.connectionState)
            )
        }

        private fun setReadyCheckboxText() {
            playerReadyCkb.text = if (playerReadyCkb.isChecked) {
                view.context.getString(R.string.ready)
            } else {
                view.context.getString(R.string.not_ready)
            }
        }
    }

    init {
        setHasStableIds(true)
    }
}
