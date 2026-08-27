// Real Unity Ads provider.
// Generates a URL to a hosted web-SDK page (Netlify) that loads Unity's
// rewarded video SDK and hands the reward back via postMessage.

package com.pulsator.ads.providers

import java.util.UUID
import kotlin.random.Random

data class UnityProvider(
    override val name: String = "unity",
    private val hostedPageUrl: String,
    private val gameId: String,
    private val interstitialPlacementId: String = "Interstitial_Android",
    private val rewardedPlacementId: String = "Rewarded_Android"
) : Provider {

    override suspend fun requestAd(app: String, placement: String, userId: String): AdResult {
        val token = UUID.randomUUID().toString()
        // 20% chance for Rewarded_Android, 80% for Interstitial_Android
        val selectedPlacementId = if (Random.nextInt(5) == 0) rewardedPlacementId else interstitialPlacementId
        val url = "$hostedPageUrl?gameId=$gameId&placementId=$selectedPlacementId&app=$app&placement=$placement&token=$token"
        return AdResult(adUrl = url, provider = name, token = token)
    }
}
