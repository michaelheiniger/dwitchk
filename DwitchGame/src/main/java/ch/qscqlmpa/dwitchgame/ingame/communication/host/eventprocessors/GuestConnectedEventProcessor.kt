package ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.ServerEvent
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GuestConnectedEventProcessor @Inject constructor() : HostCommunicationEventProcessor {

    override fun process(event: ServerEvent.CommunicationEvent): Completable {

        event as ServerEvent.CommunicationEvent.ClientConnected

        Logger.info { "Guest connected (connection ID: ${event.connectionId})." }
        return Completable.complete()
    }
}
