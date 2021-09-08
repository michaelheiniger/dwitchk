package ch.qscqlmpa.dwitchgame.ingame

import io.reactivex.rxjava3.core.Completable

interface InGameHostFacade {

    /**
     * End the current game. The game will be resumable.
     */
    fun endGame(): Completable
}
