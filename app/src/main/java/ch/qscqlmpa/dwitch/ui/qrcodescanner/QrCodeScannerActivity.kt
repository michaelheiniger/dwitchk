package ch.qscqlmpa.dwitch.ui.qrcodescanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import ch.qscqlmpa.dwitch.databinding.ActivityQrCodeScannerBinding
import ch.qscqlmpa.dwitch.ui.qrcodescanner.QrCodeScannerActivity.Companion.RESULT_CANCELLED
import ch.qscqlmpa.dwitch.ui.qrcodescanner.QrCodeScannerActivity.Companion.RESULT_OK
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import com.google.common.util.concurrent.ListenableFuture
import dagger.android.AndroidInjection
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class QrCodeScannerActivity : ComponentActivity() {

    @Inject
    lateinit var gameDiscoveryFacade: GameDiscoveryFacade

    companion object {
        const val RESULT_OK = 0
        const val RESULT_CANCELLED = 1
        const val QR_CODE_CONTENT_EXTRA = "qr-code-content-extra"
    }

    private lateinit var binding: ActivityQrCodeScannerBinding
    private lateinit var previewView: PreviewView

    private var showPermissionDenied = mutableStateOf(false)
    private var showPermissionHint = mutableStateOf(false)
    private var showError = mutableStateOf(false)
    private var showQrCodeInvalid = mutableStateOf(false)
    private var showQrCodeDecodingFailed = mutableStateOf(false)

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageAnalysis: ImageAnalysis

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            showPermissionDenied.value = false
            startCamera()
        } else {
            cancel()
        }
    }

    private lateinit var imageAnalyzer: QRCodeImageAnalyzer<GameAdvertisingInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        buildImageAnalyzer()

        binding = ActivityQrCodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        previewView = binding.previewView

        binding.composeViewTop.setContent {
            QrCodeScannerTopScreen()
        }

        binding.composeViewBottom.setContent {
            QrCodeScannerBottomScreen(
                showPermissionDenied = showPermissionDenied,
                showPermissionHint = showPermissionHint,
                showQrCodeInvalid = showQrCodeInvalid,
                showQrCodeDecodingFailed = showQrCodeDecodingFailed,
                showError = showError,
                onCameraPermissionDeniedNoClick = ::cancel,
                onCameraPermissionDeniedYesClick = ::performCameraPermissionRequest,
                onCameraPermissionHintOkClick = ::performCameraPermissionRequest,
                onCameraQrCodeInvalidTryAgainNoClick = ::cancel,
                onCameraQrCodeInvalidTryAgainYesClick = {
                    setImageAnalyzer()
                    showQrCodeInvalid.value = false
                },
                onCameraQrCodeDecodingFailedTryAgainNoClick = ::cancel,
                onCameraQrCodeDecodingFailedTryAgainYesClick = {
                    setImageAnalyzer()
                    showQrCodeDecodingFailed.value = false
                },
                onStartingCameraErrorOkClick = ::cancel
            )
        }

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        requestCamera()
    }

    override fun onBackPressed() {
        cancel()
    }

    private fun cancel() {
        setResult(RESULT_CANCELLED)
        finish()
    }

    private fun buildImageAnalyzer() {
        imageAnalyzer = QRCodeImageAnalyzer(
            object : QRCodeFoundListener<GameAdvertisingInfo> {
                override fun onQRCodeFound(data: GameAdvertisingInfo) {
                    Logger.debug { "QR-code found: $data" }
                    imageAnalysis.clearAnalyzer()

                    val intentResult = Intent()
                    intentResult.putExtra(QR_CODE_CONTENT_EXTRA, data)
                    setResult(RESULT_OK, intentResult)
                    finish()
                }

                override fun errorDecodingQrCode(qrCodeDecodingException: QrCodeDecodingException) {
                    Logger.debug { "QR-code not found" }
                    imageAnalysis.clearAnalyzer()
                    when (qrCodeDecodingException) {
                        QrCodeDecodingException.ChecksumException,
                        QrCodeDecodingException.FormatException -> showQrCodeDecodingFailed.value = true
                        QrCodeDecodingException.InvalidQrCodeException -> showQrCodeInvalid.value = true
                    }
                }
            },
            qrCodeContentConverter = gameDiscoveryFacade::deserializeGameAdvertisingInfo
        )
    }

    private fun requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showPermissionHint.value = true
            } else {
                performCameraPermissionRequest()
            }
        }
    }

    private fun performCameraPermissionRequest() {
        activityResultLauncher.launch(Manifest.permission.CAMERA)
    }

    @Suppress("TooGenericExceptionCaught")
    private fun startCamera() {
        cameraProviderFuture.addListener(
            {
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    bindCameraPreview(cameraProvider)
                } catch (e: Exception) {
                    Logger.error(e) { "Error starting camera." }
                    showError.value = true
                }
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        previewView.preferredImplementationMode = PreviewView.ImplementationMode.SURFACE_VIEW
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.createSurfaceProvider())
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(720, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        setImageAnalyzer()
        cameraProvider.bindToLifecycle((this as LifecycleOwner), cameraSelector, imageAnalysis, preview)
    }

    private fun setImageAnalyzer() {
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageAnalyzer)
    }
}

class ScanQrCodeResultContract : ActivityResultContract<Unit, QrCodeScanResult>() {
    override fun createIntent(context: Context, input: Unit) = Intent(context, QrCodeScannerActivity::class.java)

    override fun parseResult(resultCode: Int, result: Intent?): QrCodeScanResult {
        return when (resultCode) {
            RESULT_OK -> {
                val qrCodeContent = result?.getParcelableExtra<GameAdvertisingInfo>(QrCodeScannerActivity.QR_CODE_CONTENT_EXTRA)
                if (qrCodeContent != null) QrCodeScanResult.Success(qrCodeContent) else QrCodeScanResult.Error
            }
            RESULT_CANCELLED -> QrCodeScanResult.NoResult
            else -> QrCodeScanResult.Error
        }
    }
}

sealed class QrCodeScanResult {
    object NoResult : QrCodeScanResult()
    object Error : QrCodeScanResult()
    data class Success<T>(val value: T) : QrCodeScanResult()
}
