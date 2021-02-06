package ch.qscqlmpa.dwitchcommonutil

object ListUtil {

    fun <T> List<T>.shiftRightByN(N: Int): List<T> {
        val newList = ArrayList(this)
        var shift = N
        if (shift > size) shift %= size
        this.forEachIndexed { index, value ->
            val newIndex = (index + (size + shift)) % size
            newList[newIndex] = value
        }
        return newList
    }
}
