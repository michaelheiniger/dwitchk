package ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomScreen

interface GameRoomScreenBuilder {
    val screen: GameRoomScreen
}