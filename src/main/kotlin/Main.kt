// Phase 1 entry point. Ktor on Netty, four routes, three stub
// providers. In-memory session store. Render-ready.
//
// To run locally: ./gradlew run
// To build a fat JAR: ./gradlew shadowJar
// Render builds the fat JAR with the same command.

package com.pulsator.ads

import com.pulsator.ads.api.claimHandler
import com.pulsator.ads.api.healthHandler
import com.pulsator.ads.api.startHandler
import com.pulsator.ads.config.AppConfig
import com.pulsator.ads.providers.ProviderRouter
import com.pulsator.ads.providers.StubProvider
import com.pulsator.ads.session.InMemoryStore
import com.pulsator.ads.session.SessionStore
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    val cfg = AppConfig()

    // Phase 1: three stub providers with placeholder URLs.
    // Replace these with real AdMob/Unity/Playwire providers in Phase 2/3.
    val router = ProviderRouter(
        listOf(
            StubProvider("admob",    "https://example.com/admob-rewarded"),
            StubProvider("unity",    "https://example.com/unity-rewarded"),
            StubProvider("playwire", "https://example.com/playwire-rewarded")
        )
    )
    val store: SessionStore = InMemoryStore()

    // Render sets PORT; locally default to 8080.
    val port = (System.getenv("PORT") ?: "8080").toInt()
    val startedAt = System.currentTimeMillis()

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
        routing {
            startHandler(router, store, cfg)
            claimHandler(store, cfg)
            healthHandler(router, startedAt)
        }
    }.start(wait = true)
}
