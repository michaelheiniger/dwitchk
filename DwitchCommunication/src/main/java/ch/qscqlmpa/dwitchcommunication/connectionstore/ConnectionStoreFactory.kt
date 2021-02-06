package ch.qscqlmpa.dwitchcommunication.connectionstore

object ConnectionStoreFactory {

    fun createConnectionStore(): ConnectionStore {
        return ConnectionStoreImpl()
    }
}
