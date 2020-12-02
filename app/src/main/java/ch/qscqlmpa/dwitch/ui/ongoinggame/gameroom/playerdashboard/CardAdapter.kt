package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard


import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitchengine.model.card.Card
import java.util.*

internal class CardAdapter(private val listener: CardClickedListener) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private val data = ArrayList<CardItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(gameList: List<CardItem>) {
        data.clear()
        data.addAll(gameList)
        notifyDataSetChanged()
    }

    internal interface CardClickedListener {

        fun onCardClicked(card: Card)
    }

    internal class CardViewHolder(itemView: View, listener: CardClickedListener) : RecyclerView.ViewHolder(itemView) {

        private lateinit var cardItem: CardItem

        private var imageIv: ImageView = itemView.findViewById(R.id.cardIv)

        init {
            itemView.setOnClickListener {
                if (cardItem.playable) listener.onCardClicked(cardItem.card)
            }
        }

        fun bind(cardItem: CardItem) {
            this.cardItem = cardItem
            imageIv.setImageResource(ResourceMapper.getResource(this.cardItem.card))
            imageIv.contentDescription = this.cardItem.card.toString()
            imageIv.isEnabled = cardItem.playable

            if (cardItem.playable) {
                imageIv.setColorFilter(Color.argb(0, 255, 255, 255), PorterDuff.Mode.SRC_ATOP)
            } else {
                imageIv.setColorFilter(Color.argb(100, 255, 0, 0), PorterDuff.Mode.LIGHTEN)
            }
        }
    }
}

data class CardItem(val card: Card, val playable: Boolean = true)
