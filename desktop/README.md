# ArchPC — Desktop (Windows / macOS / Linux)

Um Arch Linux **limpo** na sua máquina, usando o bootstrap oficial da distro dentro
de um ambiente isolado — sem tocar no seu sistema, sem root.

## Requisitos

- **Windows**: [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- **macOS / Linux**: `proot` (via pacman/brew) **ou** Docker

## Como rodar

| Plataforma | Comando |
|---|---|
| Windows | `.\run.ps1` |
| Linux / macOS | primeiro `./bootstrap.sh`, depois `./run.sh` |
| Qualquer (Docker) | `docker build -t archpc .` e `docker run --rm -it archpc` |

## O que acontece

1. `bootstrap.sh` baixa o `archlinux-bootstrap-x86_64.tar.zst` oficial (~121 MB)
   e extrai num `rootfs/` local (por isso **não precisa de root**).
2. `run.sh` entra nesse rootfs via **proot**, dando um shell Arch puro:
   `pacman -Sy`, `pacman -S grep`, etc. — o sistema é 100% o que a distro publica.
3. Docker: a imagem `archlinux:latest` = mesma base limpa, num container de
   brinquedo, sem depender de proot instalado no host.

## Desinstalar / recomeçar

Apague a pasta `rootfs/` (e a imagem `docker rmi archpc`) — você volta ao zero,
como se fosse "reinstalar o PC". Nada do seu sistema de verdade é alterado.
