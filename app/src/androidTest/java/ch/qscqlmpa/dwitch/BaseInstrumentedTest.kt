package ch.qscqlmpa.dwitch

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStoreImpl
import ch.qscqlmpa.dwitch.persistence.*
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.reactivex.Completable
import kotlinx.serialization.json.Json
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
abstract class BaseInstrumentedTest {

    protected var gameLocalId: Long? = null
    protected var localPlayerLocalId: Long? = null
    protected val gameName = "LOTR"
    protected val hostName = "Aragorn"

    protected lateinit var db: AppRoomDatabase

    protected lateinit var store: Store

    protected lateinit var inGameStore: InGameStore

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

    private fun getDbInstance(): AppRoomDatabase {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
                .fallbackToDestructiveMigration()
                .build()
    }

    protected fun insertGameForHost(players: List<Player> = emptyList()) {
        val insertGameResult = gameDao.insertGameForHost(gameName, hostName, "127.0.0.1", 8889)
        gameLocalId = insertGameResult.gameLocalId
        players.forEach { p -> playerDao.insertPlayer(p.copy(gameLocalId = gameLocalId!!)) }
        localPlayerLocalId = insertGameResult.localPlayerLocalId
        inGameStore = InGameStoreImpl(gameLocalId!!, localPlayerLocalId!!, db, serializerFactory)
    }

    protected fun insertPlayers(players: List<Player>) {

    }

    protected fun insertGameForGuest(localGuestName: String) {
        val insertGameResult = gameDao.insertGameForGuest(gameName, localGuestName, "192.168.1.1", 8889)
        gameLocalId = insertGameResult.gameLocalId
        localPlayerLocalId = insertGameResult.localPlayerLocalId

        // Simulate  registration with host
        gameDao.updateGameCommonId(insertGameResult.gameLocalId, 1)
        playerDao.updatePlayerWithInGameId(insertGameResult.localPlayerLocalId, PlayerInGameId(1))

        inGameStore = InGameStoreImpl(gameLocalId!!, localPlayerLocalId!!, db, serializerFactory)
    }

    protected fun dudeWaitAMinute(seconds: Long = 1L) {
        Completable.fromAction { Timber.i("Waiting for %d seconds...", seconds) }
                .delay(seconds, TimeUnit.SECONDS)
                .blockingGet()
    }
}