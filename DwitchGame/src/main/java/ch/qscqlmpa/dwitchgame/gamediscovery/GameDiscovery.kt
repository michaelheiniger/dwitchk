package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchgame.gameadvertising.AdvertisedGame
import io.reactivex.rxjava3.core.Observable

internal interface GameDiscovery {
    fun listenForAdvertisedGames(): Observable<AdvertisedGame>
}
