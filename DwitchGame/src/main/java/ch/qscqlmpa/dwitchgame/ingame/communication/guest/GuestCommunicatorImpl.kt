package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import org.tinylog.kotlin.Logger

internal class GuestCommunicatorImpl constructor(
    private val commClient: CommClient,
    private val communicationEventDispatcher: GuestCommunicationEventDispatcher,
    private val communicationStateRepository: GuestCommunicationStateRepository,
    private val schedulerFactory: SchedulerFactory
) : GuestCommunicator {

    private val disposableManager = DisposableManager()

    override fun connect() {
        Logger.info { "Connect to host" }
        communicationStateRepository.updateState(GuestCommunicationState.Connecting)
        observeCommunicationEvents()
        commClient.start()
    }

    override fun disconnect() {
        Logger.info { "Disconnect from host" }
        commClient.stop()
        // Subscribed streams are disposed when ClientCommunicationEvent.DisconnectedFromHost has been processed
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
                        .doFinally {
                            if (
                                event is ClientEvent.CommunicationEvent.Stopped ||
                                event is ClientEvent.CommunicationEvent.DisconnectedFromHost ||
                                event is ClientEvent.CommunicationEvent.ConnectionError
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
}
