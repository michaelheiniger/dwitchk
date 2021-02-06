package ch.qscqlmpa.dwitchgame.ongoinggame.game.events

sealed class GuestGameEvent {
    object GameCanceled : GuestGameEvent()
    object GameLaunched : GuestGameEvent()
    object GameOver : GuestGameEvent()
}
