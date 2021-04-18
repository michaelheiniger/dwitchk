package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.MyIdlingResource
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger

internal class GuestCommunicatorImpl constructor(
    private val commClient: CommClient,
    private val messageDispatcher: MessageDispatcher,
    private val communicationEventDispatcher: GuestCommunicationEventDispatcher,
    private val communicationStateRepository: GuestCommunicationStateRepository,
    private val schedulerFactory: SchedulerFactory,
    private val idlingResource: MyIdlingResource
) : GuestCommunicator {

    private val disposableManager = DisposableManager()

    override fun connect() {
        communicationStateRepository.updateState(GuestCommunicationState.Connecting)
        observeCommunicationEvents()
        observeReceivedMessages()
        commClient.start()
    }

    override fun disconnect() {
        commClient.stop()
        disposableManager.disposeAndReset()
    }

    override fun sendMessageToHost(message: Message) {
        Logger.debug { "Sending message to host: $message" }
        commClient.sendMessageToServer(message)
    }

    override fun currentCommunicationState(): Observable<GuestCommunicationState> {
        return communicationStateRepository.observeEvents()
            .subscribeOn(schedulerFactory.io())
    }

    override fun observePlayerConnectionState(): Observable<PlayerConnectionState> {
        return communicationStateRepository.observeEvents().map { state ->
            when (state) {
                GuestCommunicationState.Connected -> PlayerConnectionState.CONNECTED
                GuestCommunicationState.Connecting,
                GuestCommunicationState.Disconnected,
                GuestCommunicationState.Error -> PlayerConnectionState.DISCONNECTED
            }
        }.subscribeOn(schedulerFactory.io())
    }

    private fun observeCommunicationEvents() {
        disposableManager.add(
            commClient.observeCommunicationEvents()
                .flatMapCompletable { event ->
                    communicationEventDispatcher.dispatch(event).subscribeOn(schedulerFactory.single())
                }
                .subscribe(
                    { Logger.debug { "Communication events stream completed" } },
                    { error -> Logger.error(error) { "Error while observing communication events" } }
                )
        )
    }

    private fun observeReceivedMessages() {
        disposableManager.add(
            commClient.observeReceivedMessages()
                .flatMapCompletable { msg ->
                    messageDispatcher.dispatch(msg)
                        .subscribeOn(schedulerFactory.single())
                        .doOnComplete {
                            Logger.debug("Decrement idling resource counter")
                            idlingResource.decrement()
                        }
                }
                .subscribe(
                    { Logger.debug { "Received messages stream completed !" } },
                    { error -> Logger.error(error) { "Error while observing received messages" } }
                )
        )
    }
}
