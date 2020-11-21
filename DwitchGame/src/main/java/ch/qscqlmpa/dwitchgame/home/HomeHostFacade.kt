package ch.qscqlmpa.dwitchgame.home

import io.reactivex.Completable

interface HomeHostFacade {

    fun hostGame(gameName: String, playerName: String, gamePort: Int): Completable
}