#!/usr/bin/env bash
set -euo pipefail
case "${1:-}" in
  debug) task=assembleDebug ;;
  release) task=assembleRelease signRelease ;;
  *) task=installDebug ;;
esac
exec ./gradlew :app:$task --no-daemon
