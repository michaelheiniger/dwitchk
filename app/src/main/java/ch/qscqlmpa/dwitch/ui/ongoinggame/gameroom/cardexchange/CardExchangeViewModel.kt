package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.CardItem
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitchgame.ongoinggame.game.PlayerDashboardFacade
import io.reactivex.rxjava3.core.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

class CardExchangeViewModel @Inject constructor(
    private val facade: PlayerDashboardFacade,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val cardsInHand = MutableLiveData<List<CardItem>>()

    //TODO: 


    fun cardsInHand(): LiveData<List<CardItem>> {
        val liveDataMerger = MediatorLiveData<List<CardItem>>()
        liveDataMerger.addSource(
            LiveDataReactiveStreams.fromPublisher(
                facade.observeDashboard()
                    .map { dashboard -> dashboard.cardsInHand.map { card -> CardItem(card, isCardSelectable(card, dashboard)) }}
                    .subscribeOn(schedulerFactory.io())
                    .observeOn(schedulerFactory.ui())
                    .doOnError { error -> Timber.e(error, "Error while observing player dashboard.") }
                    .toFlowable(BackpressureStrategy.LATEST),
            )
        ) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(cardsInHand) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    private fun isCardSelectable(card: Card, dashboard: PlayerDashboard): Boolean {
        val cardExchange = dashboard.cardExchange!!
        Timber.v("Is card $card selectable ? ${card.value()} in ${cardExchange.allowedCardValues} : ${card.value() >= dashboard.minimumCardValueAllowed.value}")
        return card.value() >= dashboard.minimumCardValueAllowed.value
                || card.name == dashboard.joker
    }

}
