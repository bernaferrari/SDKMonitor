#!/usr/bin/env bash
# Prepare JDK 21 when necessary, then build the verified Kotlin/Wasm bundle.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

java_major=0
if command -v java >/dev/null 2>&1; then
  java_major="$(java -version 2>&1 | head -n 1 | sed -E 's/.*version "([0-9]+).*/\1/')"
  [[ "$java_major" =~ ^[0-9]+$ ]] || java_major=0
fi

if (( java_major < 17 )); then
  JDK_HOME="${VERCEL_CACHE_DIR:-$HOME/.cache}/temurin-21"
  if [[ ! -x "$JDK_HOME/bin/java" ]]; then
    mkdir -p "$JDK_HOME"
    curl --fail --location --silent --show-error \
      "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse" \
      | tar -xz --strip-components=1 -C "$JDK_HOME"
  fi
  export JAVA_HOME="$JDK_HOME"
  export PATH="$JAVA_HOME/bin:$PATH"
fi

java -version
exec "$ROOT/scripts/verify-web-deploy.sh" --ci
