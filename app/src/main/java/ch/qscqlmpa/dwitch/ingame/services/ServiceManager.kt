package ch.qscqlmpa.dwitch.ingame.services

interface ServiceManager {
    fun init()
    fun stopHostService()
    fun stopGuestService()
}
