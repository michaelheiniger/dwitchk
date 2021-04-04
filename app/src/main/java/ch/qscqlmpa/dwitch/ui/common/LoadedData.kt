package ch.qscqlmpa.dwitch.ui.common

sealed class LoadedData<out T : Any> {
    object Loading : LoadedData<Nothing>()
    data class Success<out T : Any>(val data: T) : LoadedData<T>()
    object Failed : LoadedData<Nothing>()
}
