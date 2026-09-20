#!/usr/bin/env bash
# Busca o binario 'proot' do repositorio de pacotes do Termux.
# Uso: scripts/fetch-proot.sh [dir_destino]  (padrao: ./out/proot)
set -euo pipefail

DEST="${1:-out/proot}"
URL_PROOT="https://packages.termux.dev/apt/termux-main/pool/main/p/proot/proot_5.4.0-1_aarch64.deb"

mkdir -p "$(dirname "$DEST")"
echo "> baixando proot (Termux)..."
curl -fL --progress-bar "$URL_PROOT" -o /tmp/proot.deb

echo "> extraindo..."
if command -v dpkg-deb >/dev/null 2>&1; then
    dpkg-deb -x /tmp/proot.deb /tmp/proot_x
else
    mkdir -p /tmp/proot_x
    ar x /tmp/proot.deb data.tar.xz --output /tmp/proot_x
    tar -xJf /tmp/proot_x/data.tar.xz -C /tmp/proot_x
fi

cp "$(find /tmp/proot_x -type f -name proot | head -1)" "$DEST"
chmod +x "$DEST"
rm -rf /tmp/proot.deb /tmp/proot_x

echo "> ok: $DEST"
"$DEST" --version 2>/dev/null | head -1 || true
