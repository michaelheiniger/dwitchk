package ch.qscqlmpa.dwitch.ui.qrcodescanner

interface QRCodeFoundListener<T> {
    fun onQRCodeFound(data: T)
    fun errorDecodingQrCode(qrCodeDecodingException: QrCodeDecodingException) {
        // Nothing to do
    }
}
