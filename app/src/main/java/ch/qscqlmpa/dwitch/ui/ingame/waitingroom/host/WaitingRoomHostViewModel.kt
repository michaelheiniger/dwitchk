package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.navigation.HomeDestination
import ch.qscqlmpa.dwitch.ui.navigation.InGameHostDestination
import ch.qscqlmpa.dwitch.ui.navigation.ScreenNavigator
import ch.qscqlmpa.dwitch.ui.navigation.navOptionsPopUpToInclusive
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.gameadvertising.AdvertisingInfo
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class WaitingRoomHostViewModel @Inject constructor(
    private val waitingRoomHostFacade: WaitingRoomHostFacade,
    private val gameDiscoveryFacade: GameDiscoveryFacade,
    private val gameAdvertisingFacade: GameAdvertisingFacade,
    private val screenNavigator: ScreenNavigator,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _launchingGame = mutableStateOf(false)
    private val _cancelingGame = mutableStateOf(false)
    private val _canGameBeLaunched = mutableStateOf(false)
    private val _gameQrCode = mutableStateOf<Bitmap?>(null)

    val launchingGame get(): State<Boolean> = _launchingGame
    val cancelingGame get(): State<Boolean> = _cancelingGame
    val canGameBeLaunched get(): State<Boolean> = _canGameBeLaunched
    val gameQrCode get(): State<Bitmap?> = _gameQrCode

    init {
        Logger.debug { "Create WaitingRoomHostViewModel: $this" }
        gameConnectionInfoQrCode()
    }

    fun addComputerPlayer() {
        disposableManager.add(
            waitingRoomHostFacade.addComputerPlayer()
                .observeOn(uiScheduler)
                .subscribe(
                    {},
                    { error -> Logger.error(error) { "Error while adding a computer player." } }
                )
        )
    }

    fun kickPlayer(player: PlayerWrUi) {
        idlingResource.increment("Click to kick a player")
        disposableManager.add(
            waitingRoomHostFacade.kickPlayer(player)
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.info { "Player kicked successfully ($player)" } },
                    { error -> Logger.error(error) { "Error while kicking player $player." } }
                )
        )
    }

    fun launchGame() {
        _launchingGame.value = true
        disposableManager.add(
            waitingRoomHostFacade.launchGame()
                .observeOn(uiScheduler)
                .doOnTerminate { _launchingGame.value = false }
                .subscribe(
                    {
                        Logger.info { "Game launched" }
                        screenNavigator.navigate(
                            destination = InGameHostDestination.GameRoom,
                            navOptions = navOptionsPopUpToInclusive(InGameHostDestination.WaitingRoom)
                        )
                    },
                    { error -> Logger.error(error) { "Error while launching game" } },
                )
        )
    }

    fun cancelGame() {
        _cancelingGame.value = true
        disposableManager.add(
            waitingRoomHostFacade.cancelGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Game canceled" }
                        screenNavigator.navigate(
                            destination = HomeDestination.Home,
                            navOptions = navOptionsPopUpToInclusive(InGameHostDestination.WaitingRoom)
                        )
                    },
                    { error ->
                        Logger.error(error) { "Error while canceling game" }
                        _cancelingGame.value = false
                    }
                )
        )
    }

    private fun gameConnectionInfoQrCode() {
        disposableManager.add(
            gameAdvertisingFacade.observeAdvertisingInfo()
                .observeOn(uiScheduler)
                .subscribe(
                    { info ->
                        when (info) {
                            is AdvertisingInfo.Info -> _gameQrCode.value = buildQrCodeBitmap(info.serializedAd)
                            AdvertisingInfo.NoInfoAvailable -> _gameQrCode.value = null
                        }
                    },
                    { error -> Logger.error(error) { "Error while loading game advertising info" } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        gameDiscoveryFacade.stopListeningForAdvertisedGames()
        canGameBeLaunched()
    }

    private fun canGameBeLaunched() {
        disposableManager.add(
            waitingRoomHostFacade.observeGameLaunchableEvents()
                .observeOn(uiScheduler)
                .map(::isGameLaunchable)
                .subscribe(
                    { value -> _canGameBeLaunched.value = value },
                    { error -> Logger.error(error) { "Error while observing if game can be launched." } }
                )
        )
    }

    private fun isGameLaunchable(event: GameLaunchableEvent) = event.launchable

    private fun buildQrCodeBitmap(qrCodeContent: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(qrCodeContent, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}
