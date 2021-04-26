package ch.qscqlmpa.dwitchengine.utils

object CollectionUtil {

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

    /**
     * Merge the current set with the provided ones. Returns a new Set.
     */
    fun <T> Set<T>.mergeWith(vararg otherSets: Set<T>): Set<T> {
        val resultSet = mutableSetOf<T>()
        resultSet.addAll(this)
        otherSets.forEach { s -> resultSet.addAll(s) }
        return resultSet
    }

    fun <T> List<T>.concatWith(vararg otherLists: List<T>): List<T> {
        val resultList = mutableListOf<T>()
        resultList.addAll(this)
        otherLists.forEach { l -> resultList.addAll(l) }
        return resultList
    }
}
