package ch.qscqlmpa.dwitchgame.gamediscovery

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

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