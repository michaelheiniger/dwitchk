package ch.qscqlmpa.dwitch.ui.qrcode

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import android.graphics.ImageFormat
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.multi.qrcode.QRCodeMultiReader

class QRCodeImageAnalyzer<T>(
        private val listener: QRCodeFoundListener<T>,
        private val qrCodeContentConverter: (String) -> T?
) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        if (image.format == ImageFormat.YUV_420_888 || image.format == ImageFormat.YUV_422_888 || image.format == ImageFormat.YUV_444_888) {
            val byteBuffer = image.planes[0].buffer
            val imageData = ByteArray(byteBuffer.capacity())
            byteBuffer[imageData]
            val source = PlanarYUVLuminanceSource(
                imageData,
                image.width, image.height,
                0, 0,
                image.width, image.height,
                false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = QRCodeMultiReader().decode(binaryBitmap)
                val entity = qrCodeContentConverter(result.text)
                if (entity != null) {
                    listener.onQRCodeFound(entity)
                } else {
                    listener.qrCodeNotFound()
                }
            } catch (e: FormatException) {
                listener.qrCodeNotFound()
            } catch (e: ChecksumException) {
                listener.qrCodeNotFound()
            } catch (e: NotFoundException) {
                // Nothing to do, keep scanning
            }
        }
        image.close()
    }
}