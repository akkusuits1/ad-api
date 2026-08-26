package com.pulsator.ads.api

import com.pulsator.ads.config.AppConfig
import com.pulsator.ads.providers.ProviderRouter
import com.pulsator.ads.session.Session
import com.pulsator.ads.session.SessionStore
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class StartRequest(val app: String, val placement: String, val userId: String)

@Serializable
data class StartResponse(
    val adUrl: String,
    val token: String,
    val provider: String,
    val ttl: Long
)

@Serializable
data class ErrorResponse(val error: String)

fun Route.startHandler(router: ProviderRouter, store: SessionStore, cfg: AppConfig) {
    post("/v1/reward/start") {
        val req = call.receive<StartRequest>()
        if (req.app !in cfg.allowedApps) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("unknown_app"))
            return@post
        }
        val provider = router.pickProvider()
        if (provider == null) {
            call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("no_providers_configured"))
            return@post
        }
        val result = provider.requestAd(req.app, req.placement, req.userId)
        val token = UUID.randomUUID().toString()
        store.put(
            Session(
                token = token,
                app = req.app,
                userId = req.userId,
                provider = result.provider,
                openedAt = System.currentTimeMillis(),
                claimed = false
            ),
            ttlMs = cfg.sessionTtlMs
        )
        call.respond(
            StartResponse(
                adUrl = result.adUrl,
                token = token,
                provider = result.provider,
                ttl = cfg.sessionTtlMs / 1000
            )
        )
    }
}
