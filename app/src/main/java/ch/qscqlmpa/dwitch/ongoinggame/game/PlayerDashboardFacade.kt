package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import io.reactivex.Completable
import io.reactivex.Observable

interface PlayerDashboardFacade {
    fun playCard(cardPlayed: Card): Completable
    fun pickCard(): Completable
    fun passTurn(): Completable
    fun startNewRound(): Completable
    fun observeDashboard(): Observable<PlayerDashboard>
    fun observeConnectionState(): Observable<PlayerConnectionState>
}