package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.AddressType
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.RecipientType
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

internal class HostCommunicatorImpl
constructor(private val commServer: CommServer,
            private val messageDispatcher: MessageDispatcher,
            private val communicationEventDispatcher: HostCommunicationEventDispatcher,
            private val communicationStateRepository: HostCommunicationStateRepository,
            private val connectionStore: ConnectionStore,
            private val schedulerFactory: SchedulerFactory
) : HostCommunicator {

    private val disposableManager = ch.qscqlmpa.dwitchcommonutil.DisposableManager()

    override fun listenForConnections() {
        Timber.i("Listening for connections...")
        observeCommunicationEvents()
        observeReceivedMessages()
        commServer.start()
    }

    override fun closeAllConnections() {
        Timber.i("Close all connections")
        disposableManager.disposeAndReset()
        commServer.stop()
    }

    override fun sendMessage(envelopeToSend: EnvelopeToSend): Completable {
        val address = getRecipientAddress(envelopeToSend.recipient)
        return commServer.sendMessage(envelopeToSend.message, address)
    }

    override fun closeConnectionWithClient(localConnectionId: LocalConnectionId) {
        commServer.closeConnectionWithClient(localConnectionId)
    }

    override fun observeCommunicationState(): Observable<HostCommunicationState> {
        return communicationStateRepository.observeEvents()
    }

    override fun observePlayerConnectionState(): Observable<PlayerConnectionState> {
        return communicationStateRepository.observeEvents().map { state ->
            when (state) {
                HostCommunicationState.Open -> PlayerConnectionState.CONNECTED
                HostCommunicationState.Closed,
                HostCommunicationState.Error -> PlayerConnectionState.DISCONNECTED
            }
        }
    }

    private fun observeCommunicationEvents() {
        disposableManager.add(commServer.observeCommunicationEvents()
            .subscribeOn(schedulerFactory.io())
            .flatMapCompletable(communicationEventDispatcher::dispatch)
            .observeOn(schedulerFactory.ui())
            .subscribe(
                { Timber.d("Communication events stream completed") },
                { error -> Timber.e(error, "Error while observing communication events") }
            )
        )
    }

    private fun observeReceivedMessages() {
        disposableManager.add(commServer.observeReceivedMessages()
                .subscribeOn(schedulerFactory.io())
                .flatMapCompletable(messageDispatcher::dispatch)
                .subscribe(
                        { Timber.d("Received messages stream completed !") },
                        { error -> Timber.e(error, "Error while observing received messages") }
                )
        )
    }

    private fun getRecipientAddress(recipient: RecipientType): AddressType {
        return when (recipient) {
            is RecipientType.Single -> AddressType.Unicast(connectionStore.getAddress(recipient.localId)!!)
            RecipientType.All -> AddressType.Broadcast
        }
    }
}
