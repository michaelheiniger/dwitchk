package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen

interface GameRoomScreenBuilder {
    val screen: GameRoomScreen
}