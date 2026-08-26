// Phase 1 stub providers — no real SDKs, no credentials.
// Each provider returns a placeholder adUrl the user can replace
// with a real rewarded-ad URL in Phase 2+.

package com.pulsator.ads.providers

import java.util.UUID

data class AdResult(
    val adUrl: String,
    val provider: String,
    val token: String
)

interface Provider {
    val name: String
    suspend fun requestAd(app: String, placement: String, userId: String): AdResult
}

class StubProvider(
    override val name: String,
    private val placeholderUrl: String
) : Provider {
    override suspend fun requestAd(app: String, placement: String, userId: String): AdResult {
        // In Phase 1, every provider returns the same placeholder. The game opens
        // this URL in a popup; when the user comes back ≥ 5s later, the claim
        // succeeds. To test the "popup-blocked" path, deny popups in the browser.
        // To test the "too_fast" path, switch back to the game in < 5s.
        val token = UUID.randomUUID().toString()
        val url = "$placeholderUrl?provider=$name&app=$app&placement=$placement&token=$token"
        return AdResult(adUrl = url, provider = name, token = token)
    }
}
