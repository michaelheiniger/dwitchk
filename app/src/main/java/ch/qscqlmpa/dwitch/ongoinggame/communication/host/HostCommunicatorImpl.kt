package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.AddressType
import ch.qscqlmpa.dwitch.ongoinggame.events.HostCommunicationEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber

internal class HostCommunicatorImpl
constructor(private val commServer: CommServer,
            private val messageDispatcher: MessageDispatcher,
            private val communicationEventDispatcher: HostCommunicationEventDispatcher,
            private val communicationEventRepository: HostCommunicationEventRepository,
            private val localConnectionIdStore: LocalConnectionIdStore,
            private val schedulerFactory: SchedulerFactory
) : HostCommunicator {

    private val disposableManager = DisposableManager()

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
        return communicationEventRepository.observeEvents()
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
            is RecipientType.Single -> AddressType.Unicast(localConnectionIdStore.getAddress(recipient.localId)!!)
            RecipientType.All -> AddressType.Broadcast
        }
    }
}
