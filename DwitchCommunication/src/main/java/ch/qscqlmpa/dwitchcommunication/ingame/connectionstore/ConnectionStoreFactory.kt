package ch.qscqlmpa.dwitchcommunication.ingame.connectionstore

object ConnectionStoreFactory {

    fun createConnectionStore(): ConnectionStore {
        return ConnectionStoreImpl()
    }
}
