package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_PORT
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.TestWebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.WebsocketClientTestStub
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Suppress("unused")
@Module
class TestWebsocketClientModule {

    @CommunicationScope
    @Provides
    internal fun bindWebsocketClientFactory(
        @Named(HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(HOST_PORT) hostPort: Int
    ): WebsocketClientFactory {
        return TestWebsocketClientFactory(hostIpAddress, hostPort)
    }

    @CommunicationScope
    @Provides
    internal fun bindClientTestStub(
        clientFactory: WebsocketClientFactory,
        serializerFactory: SerializerFactory
    ): ClientTestStub = WebsocketClientTestStub(clientFactory as TestWebsocketClientFactory, serializerFactory)
}
