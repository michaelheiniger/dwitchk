package android.util

/**
 * Prevents the following error in unit tests containing logging statements:
 * Caused by: java.lang.RuntimeException: Method isLoggable in android.util.Log not mocked. See http://g.co/androidstudio/not-mocked for details.
 */
//FIXME: Still necessary ? --> run all tests without
object Log {

    @JvmStatic
    fun d(tag: String, msg: String): Int {
        println("DEBUG: $tag: $msg")
        return 0
    }

    @JvmStatic
    fun i(tag: String, msg: String): Int {
        println("INFO: $tag: $msg")
        return 0
    }

    @JvmStatic
    fun w(tag: String, msg: String): Int {
        println("WARN: $tag: $msg")
        return 0
    }

    @JvmStatic
    fun e(tag: String, msg: String): Int {
        println("ERROR: $tag: $msg")
        return 0
    }

    @JvmStatic
    fun isLoggable(@Suppress("UNUSED_PARAMETER") tag: String, @Suppress("UNUSED_PARAMETER") level: Int) = false
}
