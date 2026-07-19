#!/usr/bin/env bash
# Builds the deployable Wasm app and verifies its Room/OPFS prerequisites.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST="$ROOT/webApp/build/dist/wasmJs/productionExecutable"
SKIP_BUILD=false

for arg in "$@"; do
  case "$arg" in
    --ci) ;;
    --skip-build) SKIP_BUILD=true ;;
    -h|--help)
      echo "Usage: $0 [--ci] [--skip-build]"
      exit 0
      ;;
    *)
      echo "Unknown argument: $arg" >&2
      exit 1
      ;;
  esac
done

cd "$ROOT"
if [[ "$SKIP_BUILD" == false ]]; then
  ./gradlew :webApp:wasmJsBrowserDistribution \
    --no-daemon --parallel --build-cache --configuration-cache
fi
cp "$ROOT/webApp/vercel.json" "$DIST/vercel.json"

required_files=(index.html sdkmonitor-demo.js vercel.json sqlite-wasm-worker/worker.js)
for file in "${required_files[@]}"; do
  test -f "$DIST/$file" || { echo "Missing required file: $DIST/$file" >&2; exit 1; }
done

find "$DIST" -maxdepth 1 -name '*.wasm' -print -quit | grep -q . || {
  echo "Expected a Wasm binary in $DIST" >&2
  exit 1
}
grep -q 'base href="/"' "$DIST/index.html"
grep -q 'Cross-Origin-Embedder-Policy' "$DIST/vercel.json"
grep -q 'Cross-Origin-Opener-Policy' "$DIST/vercel.json"

echo "Web deploy bundle is ready: $DIST"
