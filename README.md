# ad-api

Phase 1 stub service for the Pulsator rewarded-ad API. See
`../ads_api_plan.md` for the full design and
`../ads_api_plan_addendum.md` for hosting decisions.

## What this does

Three stub providers (admob, unity, playwire) return placeholder
ad URLs. The game opens the URL in a popup, the user "watches" for
≥ 5s, switches back, and the API grants a hint. No real ad SDKs
are wired yet — that's Phase 2+.

## Endpoints

- `POST /v1/reward/start` — body `{app, placement, userId}` →
  `{adUrl, token, provider, ttl}`
- `POST /v1/reward/claim` — body `{token, app}` →
  success: `{reward: {type, amount}}` (the `ok: true` field is
  elided by kotlinx-serialization's default-value handling, so
  check for the presence of `reward` to detect success), or
  failure: `{reason}` with `reason` in
  `{expired, already_claimed, app_mismatch, too_fast}`
- `GET  /v1/balance/{app}/{userId}` — placeholder, not in Phase 1
- `GET  /health` — liveness probe

## Build

```bash
./gradlew shadowJar
java -jar build/libs/ad-api-all.jar
```

## Deploy

Push to GitHub, link the repo in Render, Render reads `render.yaml`
and deploys on every push to main.

## Test locally

```bash
./gradlew run
# in another shell:
curl http://localhost:8080/health
curl -X POST http://localhost:8080/v1/reward/start \
  -H "Content-Type: application/json" \
  -d '{"app":"pulsator","placement":"hint","userId":"test"}'
```
