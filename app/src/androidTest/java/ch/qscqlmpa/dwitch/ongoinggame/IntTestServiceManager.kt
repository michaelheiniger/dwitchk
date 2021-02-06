package ch.qscqlmpa.dwitch.ongoinggame

// class IntTestServiceManager @Inject constructor() : ServiceManager {
//
//    private lateinit var appComponent: IntTestAppComponent
//    private lateinit var onGoingGameComponent: IntTestOngoingGameComponent
//
//    fun setAppComponent(appComponent: IntTestAppComponent) {
//        this.appComponent = appComponent
//    }
//
//    fun getOnGoingGameComponent(): IntTestOngoingGameComponent {
//        return onGoingGameComponent
//    }
//
//    override fun startHostService(gameLocalId: Long, gameAdvertisingInfo: GameAdvertisingInfo, localPlayerLocalId: Long) {
//        onGoingGameComponent = appComponent.addInGameComponent(
//            OngoingGameModule(
//                PlayerRole.HOST,
//                RoomType.WAITING_ROOM,
//                gameLocalId,
//                localPlayerLocalId,
//                gameAdvertisingInfo.gamePort,
//                "0.0.0.0"
//            )
//        )
//        onGoingGameComponent.hostCommunicator.listenForConnections()
//    }
//
//    override fun stopHostService() {
//        onGoingGameComponent.hostCommunicator.closeAllConnections()
//    }
//
//    override fun goToHostGameRoom() {
//        // Nothing to do
//    }
//
//    override fun startGuestService(
//        gameLocalId: Long,
//        localPlayerLocalId: Long,
//        gamePort: Int,
//        gameIpAddress: String
//    ) {
//        onGoingGameComponent = appComponent.addInGameComponent(
//            OngoingGameModule(
//                PlayerRole.GUEST,
//                RoomType.WAITING_ROOM,
//                gameLocalId,
//                localPlayerLocalId,
//                gamePort,
//                gameIpAddress
//            )
//        )
//        // Cannot be called before hooking up the Guest with the Host
//        //onGoingGameComponent.guestCommunicator.connect()
//    }
//
//    override fun stopGuestService() {
//        onGoingGameComponent.guestCommunicator.closeConnection()
//    }
//
//    override fun goToGuestGameRoom() {
//        // Nothing to do
//    }
// }
