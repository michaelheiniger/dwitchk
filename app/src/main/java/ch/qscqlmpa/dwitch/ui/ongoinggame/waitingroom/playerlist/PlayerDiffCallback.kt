package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.playerlist

import androidx.recyclerview.widget.DiffUtil
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWr


class PlayerDiffCallback(private val oldList: List<PlayerWr>, private val newList: List<PlayerWr>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].inGameId == newList[newItemPosition].inGameId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: PlayerWr = oldList[oldItemPosition]
        val newItem: PlayerWr = newList[newItemPosition]
        return oldItem == newItem
    }
}