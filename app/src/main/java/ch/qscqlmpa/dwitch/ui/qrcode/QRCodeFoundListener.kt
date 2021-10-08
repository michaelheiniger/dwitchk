package ch.qscqlmpa.dwitch.ui.qrcode

interface QRCodeFoundListener<T> {
    fun onQRCodeFound(data: T)
    fun qrCodeNotFound() {
        // Nothing to do
    }
}
