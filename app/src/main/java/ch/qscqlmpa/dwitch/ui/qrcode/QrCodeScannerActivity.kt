package ch.qscqlmpa.dwitch.ui.qrcode

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.databinding.ActivityQrCodeScannerBinding
import ch.qscqlmpa.dwitch.ui.common.InfoDialog
import ch.qscqlmpa.dwitch.ui.common.YesNoDialog
import ch.qscqlmpa.dwitch.ui.qrcode.QrCodeScannerActivity.Companion.RESULT_CANCELLED
import ch.qscqlmpa.dwitch.ui.qrcode.QrCodeScannerActivity.Companion.RESULT_OK
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import com.google.common.util.concurrent.ListenableFuture
import org.tinylog.kotlin.Logger
import java.util.concurrent.ExecutionException

class QrCodeScannerActivity : AppCompatActivity() {

    companion object {
        const val RESULT_OK = 0
        const val RESULT_ERROR = 1
        const val RESULT_CANCELLED = 2
        const val QR_CODE_CONTENT_EXTRA = "qr-code-content-extra"
        private const val PERMISSION_REQUEST_CAMERA = 0
    }

    private lateinit var previewView: PreviewView
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var binding: ActivityQrCodeScannerBinding

    private var permissionDenied = mutableStateOf(false)
    private var showPermissionHint = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrCodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        previewView = binding.previewView

        binding.composeViewTop.setContent {
            DwitchTheme {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(stringResource(R.string.scan_qr_code_hint))
                }
            }
        }

        binding.composeViewBottom.setContent {
            DwitchTheme {
                if (permissionDenied.value) {
                    YesNoDialog(
                        text = R.string.camera_permission_denied,
                        onNoClick = { cancel() },
                        onYesClick = { performCameraPermissionRequest() }
                    )
                }
                if (showPermissionHint.value) {
                    InfoDialog(
                        title = R.string.dialog_info_title,
                        text = R.string.qr_code_camera_permission_hint,
                        onOkClick = { performCameraPermissionRequest() }
                    )
                }
            }
        }

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        requestCamera()
    }

    private fun performCameraPermissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CAMERA
        )
    }

    private fun requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showPermissionHint.value = true
            } else {
                permissionDenied.value = true
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                permissionDenied.value = true
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture.addListener(
            {
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    bindCameraPreview(cameraProvider)
                } catch (e: ExecutionException) {
                    Toast.makeText(this, "Error starting camera " + e.message, Toast.LENGTH_SHORT).show()
                } catch (e: InterruptedException) {
                    Toast.makeText(this, "Error starting camera " + e.message, Toast.LENGTH_SHORT).show()
                }
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        previewView.preferredImplementationMode = PreviewView.ImplementationMode.SURFACE_VIEW
        val preview = Preview.Builder()
            .build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.createSurfaceProvider())
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            QRCodeImageAnalyzer(object : QRCodeFoundListener {

                override fun onQRCodeFound(qrCode: String?) {
                    Logger.info { "QR-code found: $qrCode" }
                    imageAnalysis.clearAnalyzer()

                    val data = Intent()
                    data.putExtra(QR_CODE_CONTENT_EXTRA, qrCode)
                    setResult(RESULT_OK, data)
                    finish()
                }

                override fun qrCodeNotFound() {
                }
            })
        )
        val camera = cameraProvider.bindToLifecycle((this as LifecycleOwner), cameraSelector, imageAnalysis, preview)
    }

    override fun onBackPressed() {
        cancel()
    }

    private fun cancel() {
        setResult(RESULT_CANCELLED)
        finish()
    }
}

class ScanQrCodeResultContract : ActivityResultContract<Unit, QrCodeScanResult>() {
    override fun createIntent(context: Context, input: Unit) = Intent(context, QrCodeScannerActivity::class.java)

    override fun parseResult(resultCode: Int, result: Intent?): QrCodeScanResult {
        return when (resultCode) {
            RESULT_OK -> {
                val qrCodeContent = result?.getStringExtra(QrCodeScannerActivity.QR_CODE_CONTENT_EXTRA)
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
    data class Success(val qrCodeContent: String) : QrCodeScanResult()
}
