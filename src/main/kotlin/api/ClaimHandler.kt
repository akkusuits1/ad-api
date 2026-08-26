package com.pulsator.ads.api

import com.pulsator.ads.config.AppConfig
import com.pulsator.ads.session.SessionStore
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

@Serializable
data class ClaimRequest(val token: String, val app: String)

@Serializable
data class ClaimOk(val ok: Boolean = true, val reward: Reward)

@Serializable
data class ClaimFail(val ok: Boolean = false, val reason: String)

@Serializable
data class Reward(val type: String, val amount: Int)

fun Route.claimHandler(store: SessionStore, cfg: AppConfig) {
    post("/v1/reward/claim") {
        val req = call.receive<ClaimRequest>()
        val s = store.get(req.token)
        if (s == null) {
            call.respond(HttpStatusCode.OK, ClaimFail(reason = "expired"))
            return@post
        }
        if (s.claimed) {
            call.respond(HttpStatusCode.OK, ClaimFail(reason = "already_claimed"))
            return@post
        }
        if (s.app != req.app) {
            call.respond(HttpStatusCode.OK, ClaimFail(reason = "app_mismatch"))
            return@post
        }
        val elapsed = System.currentTimeMillis() - s.openedAt
        if (elapsed < cfg.minWatchMs) {
            call.respond(HttpStatusCode.OK, ClaimFail(reason = "too_fast"))
            return@post
        }
        store.markClaimed(req.token)
        call.respond(ClaimOk(reward = Reward(type = cfg.rewardType, amount = cfg.rewardAmount)))
    }
}
