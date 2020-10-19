package ch.qscqlmpa.dwitch

sealed class GuestIdTestHost(val name: String, val ipAddress: String, val port: Int)

object Guest1 : GuestIdTestHost("Boromir", "192.168.1.1", 8888)
object Guest2 : GuestIdTestHost("Celeborn", "192.168.1.2", 8888)
object Guest3 : GuestIdTestHost("Denethor", "192.168.1.3", 8888)