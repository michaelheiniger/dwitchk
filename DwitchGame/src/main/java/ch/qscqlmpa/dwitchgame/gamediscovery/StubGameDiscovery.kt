package ch.qscqlmpa.dwitchgame.gamediscovery

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable

class StubGameDiscovery : GameDiscovery {

    private val discoveredGameRelay = PublishRelay.create<AdvertisedGame>()

    fun emitGame(game: AdvertisedGame) {
        discoveredGameRelay.accept(game)
    }

    override fun listenForAdvertisedGame(): Observable<AdvertisedGame> {
        return discoveredGameRelay
    }

    override fun stopListening() {
        // Nothing to do
    }
}
