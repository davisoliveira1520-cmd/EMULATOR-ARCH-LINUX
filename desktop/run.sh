#!/usr/bin/env bash
# Entra no rootfs Arch via proot — sem root, funciona em Linux/macOS/WSL.
set -euo pipefail

ARCHPC_ROOT="${ARCHPC_ROOT:-$HOME/.archpc}"
PROOT_BIN="${ARCHPC_ROOT}/usr/bin/proot"

if [ ! -d "$ARCHPC_ROOT/usr" ]; then
  echo "Rootfs nao existe. Rode ./bootstrap.sh primeiro."
  exit 1
fi

exec "$PROOT_BIN" \
  --kill-on-exit \
  --link2symlink \
  -0 \
  -r "$ARCHPC_ROOT" \
  -b /dev -b /proc -b /sys -b /tmp \
  -w /root \
  /usr/bin/env -i HOME=/root TERM=xterm-256color PATH=/usr/bin:/bin \
  /usr/bin/bash --login
