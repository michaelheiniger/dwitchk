package ch.qscqlmpa.dwitch.gamediscovery.network

data class Packet(val message: String, val senderIpAddress: String, val senderPort: Int)
