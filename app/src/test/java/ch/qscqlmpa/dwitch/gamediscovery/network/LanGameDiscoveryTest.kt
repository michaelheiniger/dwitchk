package ch.qscqlmpa.dwitch.gamediscovery.network

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.gamediscovery.network.LanGameDiscoveryTest.Companion.gameAd1
import ch.qscqlmpa.dwitch.gamediscovery.network.LanGameDiscoveryTest.Companion.gameAd2
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import io.mockk.*
import io.reactivex.Maybe
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.SocketException


class LanGameDiscoveryTest : BaseUnitTest() {

    companion object {
        const val gameAd1 = "{\"gameCommonId\":{\"value\":23},\"gameName\":\"Kaamelott\",\"gamePort\":8889}"
        const val gameAd2 = "{\"gameCommonId\":{\"value\":54},\"gameName\":\"LOTR\",\"gamePort\":8890}"
    }

    @BeforeEach
    override fun setup() {
        super.setup()
    }

    @Test
    fun `should emit one advertised game and complete`() {
        val gameDiscovery = LanGameDiscovery(serializerFactory, TestNetworkAdapter())

        gameDiscovery.listenForAdvertisedGame()
            .doOnNext { (_, _, _) -> gameDiscovery.stopListening() }
            .test()
            .assertValue { advertisedGame ->
                isDateToday(advertisedGame.discoveryTimeAsString())
                        && advertisedGame.gameName == "Kaamelott"
                        && advertisedGame.gameCommonId == GameCommonId(23)
                        && advertisedGame.gameIpAddress == "192.168.1.1"
                        && advertisedGame.gamePort == 8889
            }
    }

    @Test
    fun `should emit two advertised games and complete`() {
        val gameDiscovery = LanGameDiscovery(serializerFactory, TestNetworkAdapter())

        gameDiscovery.listenForAdvertisedGame()
            .doOnNext { (name) ->
                if (name == "LOTR") {
                    gameDiscovery.stopListening()
                }
            }
            .test()
            .assertValueCount(2)
            .assertComplete()
    }

    @Test
    fun `should dispose NetworkAdapter resources`() {
        val networkAdapter = mockk<NetworkAdapter>()
        every { networkAdapter.close() } just runs

        val gameDiscovery = LanGameDiscovery(serializerFactory, networkAdapter)

        gameDiscovery.stopListening()

        verify { networkAdapter.close() }
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
                1 -> Maybe.just(Packet(gameAd1, "192.168.1.1", 8890))
                2 -> Maybe.just(Packet(gameAd2, "192.168.1.2", 8891))
                else -> Maybe.error(IllegalStateException())
            }
        }
    }

    override fun close() {
        // Nothing to do
    }
}