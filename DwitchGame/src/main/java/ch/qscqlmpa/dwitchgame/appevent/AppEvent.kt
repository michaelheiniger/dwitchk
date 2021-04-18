package ch.qscqlmpa.dwitchgame.appevent

sealed class AppEvent {
    data class GameCreated(val gameInfo: GameCreatedInfo) : AppEvent()
    data class GameJoined(val gameInfo: GameJoinedInfo) : AppEvent()
    object GameRoomJoinedByHost : AppEvent()
    object GameRoomJoinedByGuest : AppEvent()
    object GameLeft : AppEvent()
    object GameOverGuest : AppEvent()
    object GameOverHost : AppEvent()
    object GameCanceled : AppEvent()
}
