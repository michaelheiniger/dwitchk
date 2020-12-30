package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertising
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import javax.inject.Inject

internal class HostFacadeImpl @Inject constructor(
    private val hostCommunicator: HostCommunicator,
    private val gameAdvertising: GameAdvertising
) : HostFacade, HostCommunicator by hostCommunicator, GameAdvertising by gameAdvertising