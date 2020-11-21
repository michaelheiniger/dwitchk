package ch.qscqlmpa.dwitch

sealed class PlayerHostTest(val name: String, val ipAddress: String, val port: Int)  {
    object Guest1 : PlayerHostTest("Boromir", "192.168.1.1", 8888)
    object Guest2 : PlayerHostTest("Celeborn", "192.168.1.2", 8888)
    object Guest3 : PlayerHostTest("Denethor", "192.168.1.3", 8888)
}