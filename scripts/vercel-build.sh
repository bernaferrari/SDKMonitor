#!/usr/bin/env bash
# Prepare JDK 21 when necessary, then build the verified Kotlin/Wasm bundle.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if ! command -v java >/dev/null 2>&1; then
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
