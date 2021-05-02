package ch.qscqlmpa.dwitchgame.ongoinggame.gameevents

sealed class GuestGameEvent {
    object KickedOffGame : GuestGameEvent()
    object GameCanceled : GuestGameEvent()
    object GameLaunched : GuestGameEvent()
    object GameOver : GuestGameEvent()
}
