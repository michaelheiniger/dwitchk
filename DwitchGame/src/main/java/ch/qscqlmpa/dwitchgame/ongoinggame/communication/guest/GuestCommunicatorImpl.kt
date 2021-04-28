package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import org.tinylog.kotlin.Logger

internal class GuestCommunicatorImpl constructor(
    private val commClient: CommClient,
    private val messageDispatcher: MessageDispatcher,
    private val communicationEventDispatcher: GuestCommunicationEventDispatcher,
    private val communicationStateRepository: GuestCommunicationStateRepository,
    private val schedulerFactory: SchedulerFactory,
    private val idlingResource: DwitchIdlingResource
) : GuestCommunicator {

    private val disposableManager = DisposableManager()

    override fun connect() {
        Logger.info { "Connect to host" }
        communicationStateRepository.updateState(GuestCommunicationState.Connecting)
        observeCommunicationEvents()
        observeReceivedMessages()
        commClient.start()
    }

    override fun disconnect() {
        Logger.info { "Disconnect from host" }
        idlingResource.increment() // Event ClientCommunicationEvent.DisconnectedFromHost
        commClient.stop()
        // Subscribed streams are disposed when ServerCommunicationEvent.NoLongerListeningForConnections has been processed
    }

    override fun sendMessageToHost(message: Message) {
        Logger.debug { "Sending message to host: $message" }
        commClient.sendMessageToServer(message)
    }

    private fun observeCommunicationEvents() {
        disposableManager.add(
            commClient.observeCommunicationEvents()
                .flatMapCompletable { event ->
                    communicationEventDispatcher.dispatch(event)
                        .subscribeOn(schedulerFactory.single())
//                        .doOnComplete { if (event is ClientCommunicationEvent.DisconnectedFromHost) disconnect() }
                        .doOnComplete { idlingResource.decrement() }
                        .doFinally {
                            if (event is ClientCommunicationEvent.DisconnectedFromHost ||
                                event is ClientCommunicationEvent.ConnectionError
                            ) {
                                disposableManager.disposeAndReset()
                            }
                        }
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
                        .doOnComplete { idlingResource.decrement() }
                }
                .subscribe(
                    { Logger.debug { "Received messages stream completed !" } },
                    { error -> Logger.error(error) { "Error while observing received messages" } }
                )
        )
    }
}
