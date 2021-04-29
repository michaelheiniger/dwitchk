package ch.qscqlmpa.dwitchengine.computerplayer

interface DwitchComputerPlayerEngine {

    fun handleComputerPlayerAction(): List<ComputerPlayerActionResult>
}