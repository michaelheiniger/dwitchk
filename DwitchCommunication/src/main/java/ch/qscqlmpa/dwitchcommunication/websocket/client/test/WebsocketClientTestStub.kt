package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tinylog.kotlin.Logger

internal class WebsocketClientTestStub(
    private val client: TestWebsocketClient,
    private val serializerFactory: SerializerFactory,
    private val idlingResource: DwitchIdlingResource
) : ClientTestStub {

    override fun setConnectToServerOutcome(event: OnStartEvent) {
        client.putOnStartEvent(event)
//        idlingResource.increment()
//        Logger.debug { "Client connects to server" }
//        Completable.fromAction { client.setOnStartEvent() }
//            .subscribeOn(Schedulers.io())
//            .subscribe(
//                { Logger.debug { "Client connects to server: Completed" } },
//                { error -> Logger.error(error) { "Error while connecting client to server" }}
//            )
//        Logger.debug { "Client connects to server -->|" }
    }

    override fun serverSendsMessageToClient(message: Message) {
        idlingResource.increment()
        Logger.debug("Server sends message to client: $message")
        val messageSerialized = serializerFactory.serialize(message)
        Completable.fromAction { client.onMessage(messageSerialized) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun breakConnectionWithHost() {
        idlingResource.increment()
        client.onClose(1, "Connection lost: unknown reason", remote = false)
    }

    override fun observeMessagesSent(): Observable<String> {
        return client.observeMessagesSent()
    }
}
