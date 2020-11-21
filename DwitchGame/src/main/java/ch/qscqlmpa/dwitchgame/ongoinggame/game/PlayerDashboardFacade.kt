package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
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