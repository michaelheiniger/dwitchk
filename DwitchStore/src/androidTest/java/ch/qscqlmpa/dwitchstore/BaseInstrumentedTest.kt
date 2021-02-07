package ch.qscqlmpa.dwitchstore

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import ch.qscqlmpa.dwitchstore.dao.GameDao
import ch.qscqlmpa.dwitchstore.dao.PlayerDao
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreImpl
import ch.qscqlmpa.dwitchstore.model.Player
import ch.qscqlmpa.dwitchstore.store.Store
import ch.qscqlmpa.dwitchstore.store.StoreImpl
import ch.qscqlmpa.dwitchstore.util.SerializerFactory
import io.reactivex.rxjava3.core.Completable
import kotlinx.serialization.json.Json
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
internal abstract class BaseInstrumentedTest {

    protected var gameLocalId: Long? = null
    protected var localPlayerLocalId: Long? = null
    protected val gameName = "LOTR"
    protected val hostName = "Aragorn"

    protected lateinit var db: AppRoomDatabase

    protected lateinit var store: Store

    private lateinit var inGameStore: InGameStore

    protected lateinit var playerDao: PlayerDao

    protected lateinit var gameDao: GameDao

    protected val serializerFactory = SerializerFactory(Json)

    open fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()

        gameDao = db.gameDao()
        playerDao = db.playerDao()
        store = StoreImpl(db)
    }

    protected fun insertGameForHost(players: List<Player> = emptyList()) {
        val insertGameResult = gameDao.insertGameForHost(gameName, hostName)
        gameLocalId = insertGameResult.gameLocalId
        players.forEach { p -> playerDao.insertPlayer(p.copy(gameLocalId = gameLocalId!!)) }
        localPlayerLocalId = insertGameResult.localPlayerLocalId
        inGameStore = InGameStoreImpl(gameLocalId!!, localPlayerLocalId!!, db, serializerFactory)
    }

    protected fun dudeWaitAMinute(seconds: Long = 1L) {
        Completable.fromAction { println("Waiting for $seconds seconds...") }
            .delay(seconds, TimeUnit.SECONDS)
            .blockingSubscribe()
    }
}
