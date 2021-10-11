package ch.qscqlmpa.dwitch.ui.qrcodescanning

interface QRCodeFoundListener<T> {
    fun onQRCodeFound(data: T)
    fun errorDecodingQrCode(qrCodeDecodingException: QrCodeDecodingException) {
        // Nothing to do
    }
}
