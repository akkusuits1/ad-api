// Loads the per-app allow-list and per-provider config from YAML.
// For Phase 1, we hardcode the three stub providers in Main.kt and
// skip the YAML loader entirely. This stub is here so the file
// structure is in place for Phase 2.

package com.pulsator.ads.config

data class AppConfig(
    val allowedApps: Set<String> = setOf("pulsator", "othergame", "myapp"),
    val rewardType: String = "hint",
    val rewardAmount: Int = 1,
    val minWatchMs: Long = 5000L,
    val sessionTtlMs: Long = 600_000L
)
