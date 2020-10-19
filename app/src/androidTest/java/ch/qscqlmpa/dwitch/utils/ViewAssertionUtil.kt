package ch.qscqlmpa.dwitch.utils

object ViewAssertionUtil {
    fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }
}