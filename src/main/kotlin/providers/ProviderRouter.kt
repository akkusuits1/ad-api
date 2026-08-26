// Weighted provider selection + failover.
// For Phase 1, all providers have equal weight and the router picks
// one at random. When real SDKs are added, weights can be tuned per
// app, per placement, per geo.

package com.pulsator.ads.providers

class ProviderRouter(private val providers: List<Provider>) {

    fun pickProvider(): Provider? = providers.randomOrNull()

    fun pickProviderByName(name: String): Provider? =
        providers.firstOrNull { it.name == name }

    fun providerNames(): List<String> = providers.map { it.name }
}
