package ch.qscqlmpa.dwitchgame.common

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGameRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameAdvertisingFacadeImpl @Inject constructor(
    private val advertisedGameRepository: AdvertisedGameRepository,
) : GameAdvertisingFacade {

    override fun startListeningForAdvertisedGames() {
        advertisedGameRepository.startListeningForAdvertisedGames()
    }

    override fun stopListeningForAdvertisedGames() {
        advertisedGameRepository.stopListeningForAdvertisedGames()
    }

    override fun observeAdvertisedGames(): Observable<List<AdvertisedGame>> {
        return advertisedGameRepository.observeAdvertisedGames()
    }
}
