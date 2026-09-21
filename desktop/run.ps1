# run.ps1 — ArquPC no Windows via Docker Desktop (sem WSL, sem root).
# Uso:  .\run.ps1

$ErrorActionPreference = "Stop"
$img = "archpc:latest"

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "Docker nao encontrado. Instale o Docker Desktop: https://www.docker.com/products/docker-desktop/" -ForegroundColor Red
    exit 1
}

if (-not (docker image inspect $img 2>$null)) {
    Write-Host "> imagem nao existe, construindo '$img'..."
    docker build -t $img .
}

Write-Host "> subindo shell Arch limpo (rootfs via proot dentro do container)..."
Write-Host "> ao terminar, digite 'exit'."
docker run --rm -it --name archpc $img
