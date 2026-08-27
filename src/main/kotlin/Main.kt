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
import com.pulsator.ads.providers.UnityProvider
import com.pulsator.ads.session.InMemoryStore
import com.pulsator.ads.session.SessionStore
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    val cfg = AppConfig()

    // Phase 1: three stub providers with placeholder URLs.
    // Replace these with real AdMob/Unity/Playwire providers in Phase 2/3.
    val router = ProviderRouter(
        listOf(
            UnityProvider(
                hostedPageUrl = "https://pulsator.netlify.app/unity-rewarded.html",
                gameId = "800359755",
                interstitialPlacementId = "Interstitial_Android",
                rewardedPlacementId = "Rewarded_Android"
            )
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
        // Phase 1: anyHost() is fine — the API is fully public, returns
        // no user data, and the game is served from file://, localhost,
        // and Netlify depending on phase. Tighten in Phase 2 once we
        // know the real production host.
        install(CORS) {
            anyHost()
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowHeader(HttpHeaders.ContentType)
        }
        routing {
            startHandler(router, store, cfg)
            claimHandler(store, cfg)
            healthHandler(router, startedAt)
        }
    }.start(wait = true)
}
