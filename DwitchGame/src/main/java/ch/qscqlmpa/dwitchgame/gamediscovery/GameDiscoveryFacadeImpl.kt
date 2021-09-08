package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchgame.gameadvertising.AdvertisedGame
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameDiscoveryFacadeImpl @Inject constructor(
    private val advertisedGameRepository: AdvertisedGameRepository,
) : GameDiscoveryFacade {

    override fun startListeningForAdvertisedGames() {
        advertisedGameRepository.startListeningForAdvertisedGames()
    }

    override fun stopListeningForAdvertisedGames() {
        advertisedGameRepository.stopListeningForAdvertisedGames()
    }

    override fun observeAdvertisedGames(): Observable<List<AdvertisedGame>> {
        return advertisedGameRepository.observeAdvertisedGames()
    }

    override fun getAdvertisedGame(ipAddress: String): AdvertisedGame? {
        return advertisedGameRepository.getGame(ipAddress)
    }
}
