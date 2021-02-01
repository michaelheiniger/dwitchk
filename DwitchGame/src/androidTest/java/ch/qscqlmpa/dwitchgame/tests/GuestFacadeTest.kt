package ch.qscqlmpa.dwitchgame.tests

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.base.BaseGuestTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import org.junit.Test

class GuestFacadeTest: BaseGuestTest() {

    @Test
    fun testStartClientSuccess() {
        guestJoinsNewGameAndMovesToWaitingRoom(ClientCommunicationEvent.ConnectedToHost)
        assertGuestCommunicationState(GuestCommunicationState.Connected)
    }

    //TODO
}