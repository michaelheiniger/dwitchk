package ch.qscqlmpa.dwitchstore.ingamestore.model

import androidx.room.*
import ch.qscqlmpa.dwitchmodel.player.Player

@Entity(
    tableName = "card_exchange_answer",
    foreignKeys = [
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["player_local_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["game_local_id"])]
) //TODO: find a better name
internal data class CardExchangeAnswerStore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "player_local_id") val playerLocalId: Long,
    @ColumnInfo(name = "cards_given") val cardsGiven: String
)
