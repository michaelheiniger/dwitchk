package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
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

    override fun observeAdvertisedGames(): Observable<List<GameAdvertisingInfo>> {
        return advertisedGameRepository.observeAdvertisedGames()
    }

    override fun getAdvertisedGame(gameCommonId: GameCommonId): GameAdvertisingInfo? {
        return advertisedGameRepository.getGame(gameCommonId)
    }
}
