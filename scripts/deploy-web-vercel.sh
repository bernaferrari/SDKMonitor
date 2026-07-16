#!/usr/bin/env bash
# Deploy a verified SDK Monitor web distribution to Vercel.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST="$ROOT/webApp/build/dist/wasmJs/productionExecutable"
PREVIEW=false

if [[ "${1:-}" == "--preview" ]]; then
  PREVIEW=true
elif [[ $# -gt 0 ]]; then
  echo "Usage: $0 [--preview]" >&2
  exit 1
fi

test -f "$DIST/index.html" && test -f "$DIST/sdkmonitor-demo.js" || {
  echo "Dist not ready. Run ./scripts/verify-web-deploy.sh first." >&2
  exit 1
}

: "${VERCEL_ORG_ID:?Set VERCEL_ORG_ID}"
: "${VERCEL_PROJECT_ID:?Set VERCEL_PROJECT_ID}"

cd "$DIST"
# CI supplies VERCEL_TOKEN. On a developer machine, let the Vercel CLI use its
# refreshable signed-in session instead of passing a stale copied access token.
args=(deploy --yes)
if [[ -n "${VERCEL_TOKEN:-}" ]]; then
  args+=(--token "$VERCEL_TOKEN")
fi
if [[ "$PREVIEW" == false ]]; then args+=(--prod); fi
vercel "${args[@]}"
