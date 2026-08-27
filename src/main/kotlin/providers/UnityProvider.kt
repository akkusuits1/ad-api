// Real Unity Ads provider.
// Generates a URL to a hosted web-SDK page (Netlify) that loads Unity's
// rewarded video SDK and hands the reward back via postMessage.

package com.pulsator.ads.providers

import java.util.UUID

data class UnityProvider(
    override val name: String = "unity",
    private val hostedPageUrl: String,
    private val gameId: String,
    private val placementId: String
) : Provider {

    override suspend fun requestAd(app: String, placement: String, userId: String): AdResult {
        val token = UUID.randomUUID().toString()
        val url = "$hostedPageUrl?gameId=$gameId&placementId=$placementId&app=$app&placement=$placement&token=$token"
        return AdResult(adUrl = url, provider = name, token = token)
    }
}
