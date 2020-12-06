package ch.qscqlmpa.dwitchstore.db

import androidx.room.*
import ch.qscqlmpa.dwitchstore.ingamestore.model.DwitchEventStore
import io.reactivex.rxjava3.core.Observable

@Dao
internal abstract class DwitchEventDao {

    @Insert
    abstract fun insertEvent(event: DwitchEventStore): Long

    @Update
    abstract fun updateEvent(event: DwitchEventStore): Int

    @Query(" SELECT * FROM dwitch_event WHERE game_local_id = :gameLocalId")
    abstract fun observeDwitchEvents(gameLocalId: Long): Observable<DwitchEventStore>

    @Transaction
    fun insertEvent2(gameLocalId: Long, eventAsString: (id: Long) -> String) {
        val eventId = insertEvent(
            DwitchEventStore(
            gameLocalId = gameLocalId,
            event = ""
        ))

        val eventStore = DwitchEventStore(
            id = eventId,
            gameLocalId = gameLocalId,
            event = eventAsString(eventId)
        )
        updateEvent(eventStore)
    }

    @Query("DELETE FROM dwitch_event WHERE id = :id")
    abstract fun deleteEvent(id: Long): Int
}