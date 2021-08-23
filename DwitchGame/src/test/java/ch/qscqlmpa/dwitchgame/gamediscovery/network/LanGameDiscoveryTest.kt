package ch.qscqlmpa.dwitchgame.gamediscovery.network

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.mockk
import io.mockk.verify
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.junit.jupiter.api.Test
import java.net.SocketException

class LanGameDiscoveryTest : BaseUnitTest() {

    companion object {
        const val gameAd1 = "{\"isNew\":true,\"gameCommonId\":{\"value\":23},\"gameName\":\"Kaamelott\",\"gamePort\":8889}"
        const val gameAd2 = "{\"isNew\":true,\"gameCommonId\":{\"value\":54},\"gameName\":\"LOTR\",\"gamePort\":8890}"
    }

    @Test
    fun `should emit advertised games when subscribing to stream`() {
        val gameDiscovery = LanGameDiscovery(serializerFactory, TestNetworkAdapter())

        gameDiscovery.listenForAdvertisedGames()
            .take(1)
            .test()
            .assertValue { advertisedGame ->
                isDateToday(advertisedGame.discoveryTimeAsString()) &&
                    advertisedGame.gameName == "Kaamelott" &&
                    advertisedGame.gameCommonId == GameCommonId(23) &&
                    advertisedGame.gameIpAddress == "192.168.1.1" &&
                    advertisedGame.gamePort == 8889
            }
    }

    @Test
    fun `should dispose NetworkAdapter resources when stream is disposed`() {
        val networkAdapter = mockk<NetworkAdapter>(relaxed = true)

        val gameDiscovery = LanGameDiscovery(serializerFactory, networkAdapter)
        gameDiscovery.listenForAdvertisedGames().test().dispose()

        verify { networkAdapter.close() }
    }

    private fun isDateToday(date: String): Boolean {
        val now = LocalDateTime.now()
        val fmt: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")
        return date.startsWith(fmt.print(now))
    }

    internal class TestNetworkAdapter : NetworkAdapter {

        private var counter = 0

        @Throws(SocketException::class)
        override fun bind(listeningPort: Int) {
            // Nothing to do
        }

        override fun receive(): Packet {
            counter++
            return when (counter) {
                1 -> Packet(gameAd1, "192.168.1.1", 8890)
                2 -> Packet(gameAd2, "192.168.1.2", 8891)
                else -> throw IllegalStateException()
            }
        }

        override fun close() {
            // Nothing to do
        }
    }
}
