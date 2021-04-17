package ch.qscqlmpa.dwitchgame.ongoinggame.common

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import javax.inject.Inject

internal class GuestFacadeImpl @Inject constructor(
    private val guestCommunicator: GuestCommunicator,
) : GuestFacade, GuestCommunicator by guestCommunicator