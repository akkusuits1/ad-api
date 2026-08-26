// Session storage. Phase 1 uses an in-memory map; Phase 2+ swaps in
// Redis. The interface is the same so the handler code does not
// change when we move to Upstash.

package com.pulsator.ads.session

import java.util.concurrent.ConcurrentHashMap

data class Session(
    val token: String,
    val app: String,
    val userId: String,
    val provider: String,
    val openedAt: Long,
    var claimed: Boolean = false
)

interface SessionStore {
    fun put(session: Session, ttlMs: Long)
    fun get(token: String): Session?
    fun markClaimed(token: String): Boolean
}

class InMemoryStore : SessionStore {
    private val map = ConcurrentHashMap<String, Session>()

    override fun put(session: Session, ttlMs: Long) {
        map[session.token] = session
        // Best-effort cleanup. A real Redis impl uses SET EX; here we
        // just schedule a remove. Acceptable for Phase 1 because the
        // sandbox is small and the process is short-lived.
        Thread {
            Thread.sleep(ttlMs)
            map.remove(session.token)
        }.start()
    }

    override fun get(token: String): Session? = map[token]

    override fun markClaimed(token: String): Boolean {
        val s = map[token] ?: return false
        if (s.claimed) return false
        s.claimed = true
        return true
    }
}
