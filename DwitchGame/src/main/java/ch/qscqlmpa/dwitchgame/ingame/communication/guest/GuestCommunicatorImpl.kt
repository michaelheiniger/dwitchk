package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.ingame.CommClient
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchgame.ingame.di.InstanceQualifiers
import org.tinylog.kotlin.Logger
import javax.inject.Inject
import javax.inject.Named

@InGameScope
internal class GuestCommunicatorImpl @Inject constructor(
    @Named(InstanceQualifiers.ADVERTISED_GAME) private val initialAdvertisedGame: GameAdvertisingInfo,
    private val commClient: CommClient,
    private val communicationEventDispatcher: GuestCommunicationEventDispatcher,
    private val communicationStateRepository: GuestCommunicationStateRepository,
    private val schedulerFactory: SchedulerFactory
) : GuestCommunicator {

    private val disposableManager = DisposableManager()

    override fun connect(advertisedGame: GameAdvertisingInfo) {
        Logger.info { "Connecting to host..." }
        communicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.ConnectingToServer)
        observeCommunicationEvents()
        commClient.start(advertisedGame.gameIpAddress, advertisedGame.gamePort)
    }

    override fun connect() {
        connect(initialAdvertisedGame)
    }

    override fun disconnect() {
        Logger.info { "Disconnecting from host..." }
        commClient.stop()
        // Subscribed streams are disposed when ClientCommunicationEvent.Stopped has been processed
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
                                event is ClientEvent.CommunicationEvent.DisconnectedFromServer ||
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
