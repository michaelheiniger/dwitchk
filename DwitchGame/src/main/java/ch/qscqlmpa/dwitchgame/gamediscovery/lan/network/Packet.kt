package ch.qscqlmpa.dwitchgame.gamediscovery.lan.network

data class Packet(val message: String, val senderIpAddress: String, val senderPort: Int)
