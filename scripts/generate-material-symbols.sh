#!/usr/bin/env bash

# Regenerates the local Material Symbols Rounded ImageVectors from Google's
# Compose endpoint. SDKMonitor keeps filled and outlined vectors for each icon in
# one <IconName>Icon.kt file and exposes them through MaterialSymbols.kt.

set -euo pipefail

readonly ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
readonly OUTPUT_DIR="$ROOT_DIR/shared/src/commonMain/kotlin/com/bernaferrari/sdkmonitor/ui/icons"
readonly BASE_URL="https://fonts.gstatic.com/render/v1/Material+Symbols+Rounded/24dp"
readonly PACKAGE_NAME="com.bernaferrari.sdkmonitor.ui.icons"

readonly FILLED_ICONS=(
  android apps arrow_back arrow_forward brightness_auto calendar_month calendar_today
  camera_alt chat check clear close dark_mode date_range download email error extension
  headset history info keyboard_arrow_down keyboard_arrow_right language light_mode lock
  monitor_heart music_note open_in_new palette person place play_arrow play_circle refresh
  search search_off security settings shop sort_by_alpha storefront sync sync_disabled
  timeline trending_up tune warning
)

readonly OUTLINED_ICONS=(
  apps calendar_month calendar_today date_range person trending_up tune
)

readonly AUTO_MIRRORED_FILLED_ICONS=(
  arrow_back arrow_forward chat keyboard_arrow_right open_in_new trending_up
)

readonly AUTO_MIRRORED_OUTLINED_ICONS=(
  trending_up
)

CHECK_ONLY=0

usage() {
  cat <<'EOF'
Usage: scripts/generate-material-symbols.sh [--check]

Regenerate Material Symbols Kotlin sources. With --check, verify that the
checked-in sources match the endpoint without changing any files.
EOF
}

case "${1:-}" in
  "") ;;
  --check) CHECK_ONLY=1 ;;
  -h|--help) usage; exit 0 ;;
  *) usage >&2; exit 2 ;;
esac
[[ $# -le 1 ]] || { usage >&2; exit 2; }

require_command() {
  command -v "$1" >/dev/null 2>&1 || {
    printf 'Required command not found: %s\n' "$1" >&2
    exit 1
  }
}

for command in cat cmp curl grep mkdir mktemp mv perl rm sed; do
  require_command "$command"
done

validate_icon_list() {
  local list_name="$1"
  shift
  local icon
  local seen=' '

  for icon in "$@"; do
    if [[ ! "$icon" =~ ^[a-z][a-z0-9_]*$ ]]; then
      printf 'Invalid icon name in %s: %s\n' "$list_name" "$icon" >&2
      return 1
    fi
    if [[ "$seen" == *" $icon "* ]]; then
      printf 'Duplicate icon in %s: %s\n' "$list_name" "$icon" >&2
      return 1
    fi
    seen+="$icon "
  done
}

validate_icon_lists() {
  local icon
  local candidate
  local pascal
  local other_pascal

  validate_icon_list FILLED_ICONS "${FILLED_ICONS[@]}"
  validate_icon_list OUTLINED_ICONS "${OUTLINED_ICONS[@]}"
  validate_icon_list AUTO_MIRRORED_FILLED_ICONS "${AUTO_MIRRORED_FILLED_ICONS[@]}"
  validate_icon_list AUTO_MIRRORED_OUTLINED_ICONS "${AUTO_MIRRORED_OUTLINED_ICONS[@]}"

  for icon in "${AUTO_MIRRORED_FILLED_ICONS[@]}"; do
    if [[ " ${FILLED_ICONS[*]} " != *" $icon "* ]]; then
      printf 'Auto-mirrored filled icon has no filled source: %s\n' "$icon" >&2
      return 1
    fi
  done
  for icon in "${AUTO_MIRRORED_OUTLINED_ICONS[@]}"; do
    if [[ " ${OUTLINED_ICONS[*]} " != *" $icon "* ]]; then
      printf 'Auto-mirrored outlined icon has no outlined source: %s\n' "$icon" >&2
      return 1
    fi
  done

  # Different snake_case names must not collapse to the same Kotlin/file name.
  for icon in "${FILLED_ICONS[@]}"; do
    pascal="$(to_pascal_case "$icon")"
    for candidate in "${FILLED_ICONS[@]}"; do
      [[ "$icon" == "$candidate" ]] && continue
      other_pascal="$(to_pascal_case "$candidate")"
      if [[ "$pascal" == "$other_pascal" ]]; then
        printf 'Icon names collide after Kotlin conversion: %s and %s\n' \
          "$icon" "$candidate" >&2
        return 1
      fi
    done
  done
}

to_pascal_case() {
  printf '%s' "$1" | perl -pe 's/(^|_)([a-z0-9])/$2 eq "" ? $1 : uc($2)/ge'
}

download_vector() {
  local icon="$1"
  local fill="$2"
  local output="$3"
  local url="$BASE_URL/${icon}.kt?var=opsz,wght,FILL,GRAD,ROND@24,400,${fill},0,50"

  curl \
    --compressed \
    --fail \
    --silent \
    --show-error \
    --location \
    --retry 3 \
    --retry-delay 1 \
    --connect-timeout 15 \
    --max-time 60 \
    --user-agent 'SDKMonitor Material Symbols generator' \
    "$url" \
    --output "$output"
}

normalize_vector() {
  local source="$1"
  local icon="$2"
  local style="$3"
  local pascal
  local style_prefix
  pascal="$(to_pascal_case "$icon")"
  case "$style" in
    Filled) style_prefix=filled ;;
    Outlined) style_prefix=outlined ;;
    *) printf 'Unsupported style: %s\n' "$style" >&2; exit 1 ;;
  esac

  # The endpoint uses the Material icon's snake_case name for the Kotlin
  # property. Normalize it to the lowerCamelCase name used by this project.
  perl -pi -e "s/^package .*/package ${PACKAGE_NAME}/" "$source"
  perl -pi -e "s/public val ${icon}\\b/public val ${style_prefix}${pascal}/g" "$source"
  perl -pi -e "s/_${icon}\\b/_${style_prefix}${pascal}/g" "$source"
  perl -pi -e 's/^public val /internal val /; s/^private var /internal var /' "$source"

  case "$icon" in
    arrow_back|arrow_forward|chat|keyboard_arrow_right|open_in_new|trending_up)
      perl -0pi -e 's/(viewportHeight = 24f,\n)/$1          autoMirror = true,\n/' "$source"
      ;;
  esac

  if ! grep -Fq "internal val ${style_prefix}${pascal}: ImageVector" "$source" ||
    ! grep -Fq "internal var _${style_prefix}${pascal}: ImageVector? = null" "$source"; then
    printf 'Google response format changed for %s (%s)\n' "$icon" "$style" >&2
    return 1
  fi
}

vector_body() {
  # Keep imports/package/header under our control and append only the generated
  # declarations from Google's response.
  local source="$1"
  if [[ "$(grep -Fc '@Suppress("CheckReturnValue")' "$source")" -ne 1 ]]; then
    printf 'Expected one generated vector declaration in %s\n' "$source" >&2
    return 1
  fi
  sed -n '/^@Suppress("CheckReturnValue")/,$p' "$source"
}

generate_icon() {
  local icon="$1"
  local destination_dir="$2"
  local pascal
  local output
  local temp_dir="$WORK_DIR/downloads/$icon"
  local has_filled=0
  local has_outlined=0
  pascal="$(to_pascal_case "$icon")"
  output="$destination_dir/${pascal}Icon.kt"
  mkdir -p "$temp_dir"

  if [[ " ${FILLED_ICONS[*]} " == *" $icon "* ]]; then
    has_filled=1
    download_vector "$icon" 1 "$temp_dir/filled.kt"
    normalize_vector "$temp_dir/filled.kt" "$icon" Filled
  fi
  if [[ " ${OUTLINED_ICONS[*]} " == *" $icon "* ]]; then
    has_outlined=1
    download_vector "$icon" 0 "$temp_dir/outlined.kt"
    normalize_vector "$temp_dir/outlined.kt" "$icon" Outlined
  fi

  {
    cat <<EOF
package ${PACKAGE_NAME}

// Generated from Google Material Symbols Rounded's Kotlin vector endpoint.
// The FILL axis is explicit: FILL=1 for Filled and FILL=0 for Outlined.
// opsz=24, wght=400, GRAD=0, ROND=50.
// Source: ${BASE_URL}/<name>.kt

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

EOF
    if [[ "$has_filled" -eq 1 ]]; then
      vector_body "$temp_dir/filled.kt"
    fi
    if [[ "$has_outlined" -eq 1 ]]; then
      printf '\n'
      vector_body "$temp_dir/outlined.kt"
    fi
  } > "$output"
}

generate_facade() {
  local destination_dir="$1"
  local output="$destination_dir/MaterialSymbols.kt"

  {
    cat <<EOF
package ${PACKAGE_NAME}

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Material Symbols Rounded icons generated from Google's Kotlin vector endpoint.
 *
 * [Filled] maps to FILL=1 (active/selected); [Outlined] maps to FILL=0
 * (inactive/unselected). These are the Material Symbols FILL axis presets.
 */
object MaterialSymbols {
    object Filled {
EOF
    for icon in "${FILLED_ICONS[@]}"; do
        printf '        val %s: ImageVector get() = filled%s\n' \
        "$(to_pascal_case "$icon")" "$(to_pascal_case "$icon")"
    done
    cat <<'EOF'
    }

    object Outlined {
EOF
    for icon in "${OUTLINED_ICONS[@]}"; do
      printf '        val %s: ImageVector get() = outlined%s\n' \
        "$(to_pascal_case "$icon")" "$(to_pascal_case "$icon")"
    done
    cat <<'EOF'
    }

    object AutoMirrored {
        object Filled {
EOF
    for icon in "${AUTO_MIRRORED_FILLED_ICONS[@]}"; do
      printf '            val %s: ImageVector get() = filled%s\n' \
        "$(to_pascal_case "$icon")" "$(to_pascal_case "$icon")"
    done
    cat <<'EOF'
        }

        object Outlined {
EOF
    for icon in "${AUTO_MIRRORED_OUTLINED_ICONS[@]}"; do
      printf '            val %s: ImageVector get() = outlined%s\n' \
        "$(to_pascal_case "$icon")" "$(to_pascal_case "$icon")"
    done
    cat <<'EOF'
        }
    }
}
EOF
  } > "$output"
}

is_expected_icon_file() {
  local filename="$1"
  local icon

  for icon in "${FILLED_ICONS[@]}"; do
    [[ "$filename" == "$(to_pascal_case "$icon")Icon.kt" ]] && return 0
  done
  for icon in "${OUTLINED_ICONS[@]}"; do
    [[ "$filename" == "$(to_pascal_case "$icon")Icon.kt" ]] && return 0
  done
  return 1
}

validate_outputs() {
  local output_dir="$1"
  local icon
  local pascal
  local file
  local failed=0

  for icon in "${FILLED_ICONS[@]}"; do
    pascal="$(to_pascal_case "$icon")"
    file="$output_dir/${pascal}Icon.kt"
    if [[ ! -f "$file" ]] || ! grep -Fq "internal val filled${pascal}" "$file"; then
      printf 'Missing filled output for %s\n' "$icon" >&2
      failed=1
    fi
  done

  for icon in "${OUTLINED_ICONS[@]}"; do
    pascal="$(to_pascal_case "$icon")"
    file="$output_dir/${pascal}Icon.kt"
    if [[ ! -f "$file" ]] || ! grep -Fq "internal val outlined${pascal}" "$file"; then
      printf 'Missing outlined output for %s\n' "$icon" >&2
      failed=1
    fi
  done

  file="$output_dir/MaterialSymbols.kt"
  if [[ ! -f "$file" ]]; then
    printf 'Missing MaterialSymbols facade\n' >&2
    failed=1
  else
    for icon in "${FILLED_ICONS[@]}"; do
      pascal="$(to_pascal_case "$icon")"
      if ! grep -Fq "val ${pascal}: ImageVector get() = filled${pascal}" "$file"; then
        printf 'Missing filled facade entry for %s\n' "$icon" >&2
        failed=1
      fi
    done
    for icon in "${OUTLINED_ICONS[@]}"; do
      pascal="$(to_pascal_case "$icon")"
      if ! grep -Fq "val ${pascal}: ImageVector get() = outlined${pascal}" "$file"; then
        printf 'Missing outlined facade entry for %s\n' "$icon" >&2
        failed=1
      fi
    done
    for icon in "${AUTO_MIRRORED_FILLED_ICONS[@]}"; do
      pascal="$(to_pascal_case "$icon")"
      if ! grep -Fq "val ${pascal}: ImageVector get() = filled${pascal}" "$file"; then
        printf 'Missing auto-mirrored facade entry for %s\n' "$icon" >&2
        failed=1
      fi
    done
    for icon in "${AUTO_MIRRORED_OUTLINED_ICONS[@]}"; do
      pascal="$(to_pascal_case "$icon")"
      if ! grep -Fq "val ${pascal}: ImageVector get() = outlined${pascal}" "$file"; then
        printf 'Missing auto-mirrored outlined facade entry for %s\n' "$icon" >&2
        failed=1
      fi
    done
  fi

  for file in "$output_dir"/*Icon.kt; do
    [[ -e "$file" ]] || continue
    [[ "${file##*/}" == "MaterialIcon.kt" ]] && continue
    if ! is_expected_icon_file "${file##*/}"; then
      printf 'Unexpected generated icon file: %s\n' "${file##*/}" >&2
      failed=1
    fi
  done

  return "$failed"
}

validate_existing_layout() {
  local file
  [[ -d "$OUTPUT_DIR" ]] || return 0

  for file in "$OUTPUT_DIR"/*Icon.kt; do
    [[ -e "$file" ]] || continue
    [[ "${file##*/}" == "MaterialIcon.kt" ]] && continue
    if ! is_expected_icon_file "${file##*/}"; then
      printf 'Unexpected generated icon file: %s\n' "${file##*/}" >&2
      return 1
    fi
  done
}

publish_outputs() {
  local filename
  local staged
  local destination

  mkdir -p "$OUTPUT_DIR"
  for staged in "$STAGING_DIR"/*.kt; do
    filename="${staged##*/}"
    destination="$OUTPUT_DIR/$filename"
    if [[ -f "$destination" ]] && cmp -s "$staged" "$destination"; then
      continue
    fi
    mv "$staged" "$destination"
  done
}

check_outputs() {
  local filename
  local staged
  local destination
  local failed=0

  for staged in "$STAGING_DIR"/*.kt; do
    filename="${staged##*/}"
    destination="$OUTPUT_DIR/$filename"
    if [[ ! -f "$destination" ]] || ! cmp -s "$staged" "$destination"; then
      printf 'Generated output is stale: %s\n' "$destination" >&2
      failed=1
    fi
  done
  return "$failed"
}

validate_icon_lists
validate_existing_layout

readonly WORK_DIR="$(mktemp -d "${TMPDIR:-/tmp}/netguard-material-symbols.XXXXXX")"
readonly STAGING_DIR="$WORK_DIR/output"
cleanup() {
  rm -rf "$WORK_DIR"
}
trap cleanup EXIT
trap 'exit 129' HUP
trap 'exit 130' INT
trap 'exit 143' TERM
mkdir -p "$STAGING_DIR" "$WORK_DIR/downloads"

for icon in "${FILLED_ICONS[@]}"; do
  generate_icon "$icon" "$STAGING_DIR"
done
for icon in "${OUTLINED_ICONS[@]}"; do
  if [[ " ${FILLED_ICONS[*]} " != *" $icon "* ]]; then
    generate_icon "$icon" "$STAGING_DIR"
  fi
done

generate_facade "$STAGING_DIR"
validate_outputs "$STAGING_DIR"

if [[ "$CHECK_ONLY" -eq 1 ]]; then
  check_outputs
  printf 'Material Symbols outputs are up to date.\n'
  exit 0
fi

publish_outputs

printf 'Generated %d filled and %d outlined Material Symbols in %s\n' \
  "${#FILLED_ICONS[@]}" "${#OUTLINED_ICONS[@]}" "$OUTPUT_DIR"
