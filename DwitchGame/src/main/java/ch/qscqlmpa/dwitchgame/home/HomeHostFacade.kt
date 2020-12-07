package ch.qscqlmpa.dwitchgame.home

import io.reactivex.rxjava3.core.Completable


interface HomeHostFacade {

    fun hostGame(gameName: String, playerName: String, gamePort: Int): Completable
}