package ch.qscqlmpa.dwitch.ongoinggame.services

import android.content.Intent
import ch.qscqlmpa.dwitch.common.CommonExtraConstants
import ch.qscqlmpa.dwitch.service.BaseService

abstract class BaseInGameService : BaseService() {

    protected fun getGameLocalId(intent: Intent): Long {
        val gameLocalId = intent.getLongExtra(CommonExtraConstants.EXTRA_GAME_LOCAL_ID, 0)

        if (gameLocalId == 0L) {
            throw IllegalArgumentException("The intent to start the service does not specify a game local-ID")
        }
        return gameLocalId
    }

    protected fun getLocalPlayerLocalId(intent: Intent): Long {
        val localPlayerLocalId = intent.getLongExtra(CommonExtraConstants.EXTRA_LOCAL_PLAYER_LOCAL_ID, 0)

        if (localPlayerLocalId == 0L) {
            throw IllegalArgumentException("The intent to start the service does not specify a local player local-ID")
        }
        return localPlayerLocalId
    }
}