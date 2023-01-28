package io.collective

import java.time.Clock

class SimpleAgedKache() {
    @JvmField
    var cache: Array<ExpirableEntry?>

    @JvmField
    var size = 0

    @JvmField
    var clock: Clock? = null

    constructor(clock: Clock?) : this() {
        this.clock = clock
    }

    init {
        cache = arrayOfNulls(10)
    }

    fun put(key: Any?, value: Any?, retentionInMillis: Int) {
        if (size == 10) {
            return
        }
        cache[size]  = ExpirableEntry(key as String, value as String, retentionInMillis)
        size++
    }

    fun isEmpty(): Boolean {
        removeExpiredEntries()
        return size == 0
    }

    fun size(): Int {
        removeExpiredEntries()
        return size
    }

    private fun removeExpiredEntries() {
        if (clock == null) return
        val cache1 = arrayOfNulls<ExpirableEntry>(10)
        var count = 0
        for (i in 0 until size) {
            val entry = cache[i]
            if (entry!!.retentionInMillis > clock!!.instant().epochSecond) {
                cache1[count++] = entry
            }
        }
        cache = cache1
        size = count
    }

     fun get(key: Any): Any? {
        removeExpiredEntries()
        for (i in 0 until size) {
            val cacheEntry = cache[i]
            if (cacheEntry != null && cacheEntry.key == key) {
                return cacheEntry.value
            }
        }
        return null
    }

    inner class ExpirableEntry(@JvmField var key: String, @JvmField var value: String, retentionInMillis: Int) {
        @JvmField
        var retentionInMillis: Long

        init {
            var seconds: Long = 0
            if (clock != null) {
                seconds = clock!!.instant().epochSecond
            }
            this.retentionInMillis = retentionInMillis / 1000 + seconds
        }
    }
}