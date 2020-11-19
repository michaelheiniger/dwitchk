package ch.qscqlmpa.dwitch.home

import io.reactivex.Completable

interface HomeHostFacade {

    fun hostGame(gameName: String, playerName: String): Completable
}