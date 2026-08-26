package com.pulsator.ads.api

import com.pulsator.ads.providers.ProviderRouter
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val ok: Boolean,
    val uptime: Long,
    val providers: List<String>
)

fun Route.healthHandler(router: ProviderRouter, startedAt: Long) {
    get("/health") {
        call.respond(
            HealthResponse(
                ok = true,
                uptime = (System.currentTimeMillis() - startedAt) / 1000,
                providers = router.providerNames()
            )
        )
    }
}
