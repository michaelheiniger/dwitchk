package ch.qscqlmpa.dwitch.gamediscovery.network

import io.reactivex.Maybe
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.net.SocketException


class LanGameDiscoveryTest {

    @Test
    fun `should emit one advertised game and complete`() {

        val gameDiscovery = LanGameDiscovery(TestNetworkAdapter())

        gameDiscovery.listenForAdvertisedGame()
                .doOnNext { (_, _, _) -> gameDiscovery.stopListening() }
                .test().assertValue { advertisedGame ->
                    isDateToday(advertisedGame.discoveryTimeAsString())
                            && advertisedGame.name == "message 1"
                            && advertisedGame.ipAddress == "192.168.1.1"
                            && advertisedGame.port == 8890
                }
    }

    @Test
    fun `should emit two advertised games and complete`() {

        val gameDiscovery = LanGameDiscovery(TestNetworkAdapter())

        gameDiscovery.listenForAdvertisedGame()
                .doOnNext { (name) ->
                    if (name == "message 2") {
                        gameDiscovery.stopListening()
                    }
                }
                .test()
                .assertValueCount(2)
                .assertComplete()
    }

    @Test
    fun `should dispose NetworkAdapter resources`() {

        val networkAdapter = mock(NetworkAdapter::class.java)

        val gameDiscovery = LanGameDiscovery(networkAdapter)

        gameDiscovery.stopListening()

        verify(networkAdapter).close()
    }

    private fun isDateToday(date: String): Boolean {
        val now = LocalTime.now()
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")
        return date.startsWith(fmt.print(now))
    }
}

internal class TestNetworkAdapter : NetworkAdapter {

    private var counter = 0

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        // Nothing to do
    }

    override fun receive(): Maybe<Packet> {

        return Maybe.defer {
            counter++
            when (counter) {
                1 -> Maybe.just(Packet("message 1", "192.168.1.1", 8890))
                2 -> Maybe.just(Packet("message 2", "192.168.1.2", 8891))
                else -> Maybe.error(IllegalStateException())
            }
        }
    }

    override fun close() {
        // Nothing to do
    }
}