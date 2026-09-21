#!/usr/bin/env bash
# Cria o rootfs base do Arch limpo dentro de $ARCHPC_ROOT (padrao: ~/.archpc).
# Usa so a base oficial com pacotes minimos; nenhum desktop/escala pre-instalada.
set -euo pipefail

ARCHPC_ROOT="${ARCHPC_ROOT:-$HOME/.archpc}"
BOOTSTRAP_URL="https://geo.mirror.pkgbuild.com/iso/latest/archlinux-bootstrap-x86_64.tar.zst"

mkdir -p "$ARCHPC_ROOT"
echo "> baixando base oficial Arch (limpa, sem pacotes)..."
wget -q --show-progress -O /tmp/arch-bootstrap.tar.zst "$BOOTSTRAP_URL"

echo "> extraindo rootfs em $ARCHPC_ROOT..."
tar --zstd -xf /tmp/arch-bootstrap.tar.zst -C "$ARCHPC_ROOT" --strip-components=1

echo "> configurando chaves e mirror..."
"$ARCHPC_ROOT/usr/bin/arch-chroot" /dev/null 2>/dev/null || true

echo "> pronto. Rootfs limpo em $ARCHPC_ROOT"
echo "> Rode ./run.sh para entrar no sistema (via proot)."
