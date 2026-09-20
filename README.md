# ArchPC

> Um "emulador de PC" que roda um **Arch Linux limpo** — igual você baixa a distro — em qualquer dispositivo.

ArchPC é um projeto de dois blocos que compartilham a mesma ideia:

1. **Android** — um app que baixa o **bootstrap oficial do Arch Linux**, extrai num rootfs isolado e entra nele via `proot` (sem root, sem virtualização). Você recebe um Arch *100% limpo*: nada pré-instalado. `pacman -S o-que-quiser`.
2. **Desktop (Windows / macOS / Linux)** — scripts que baixam o mesmo bootstrap/sistema base limpo e sobem num container ou via `proot`, te dando um terminal Arch puro em qualquer máquina.

O foco é ser **um emulador mesmo** — uma máquina Arch "virgem" para explorar, estudar ou brincar. Quando quiser recomeçar, é só apagar o rootfs e reinstalar (como rebaixar a ISO).

---

## Conceito

| Referência | O que pegamos |
|---|---|
| [CypherpunkArmory/UserLAnd](https://github.com/cypherpunkarmory/userland) | Rodar distros Linux no Android **sem root** usando `proot` |
| [windows-ui/HyperDroid](https://github.com/windows-ui/HyperDroid) | A experiência de "desktop/PC" por cima |

ArchPC junta os dois: o cérebro do UserLAnd (rootfs + proot) e a aparência de um PC (desktop/launcher) — com a regra de que o sistema instalado é **sempre limpo**, exatamente como o tarball oficial da distro.

Diferente de outras soluções, o ArchPC **não emula um PC por cima de outro**: ele monta a *mesma* base que a distribuição publica (`archlinux-bootstrap-*.tar.zst` no desktop, `ArchLinuxARM-*-latest.tar.gz` em celulares ARM), então o que você vê é o Arch legítimo, sem customizações.

---

## Como instalar o Arch (a parte "igual você baixa a distro")

### Pagina oficial / espelhos usados

| Plataforma | Artefato baixado | Tamanho | Origem |
|---|---|---|---|
| Desktop x86_64 | `archlinux-bootstrap-x86_64.tar.zst` | ~121 MB | https://geo.mirror.pkgbuild.com/iso/latest/ |
| Android arm64-v8a | `ArchLinuxARM-aarch64-latest.tar.gz` | ~790 MB | https://mirror.archlinuxarm.org/os/ |
| Android x86_64 | `archlinux-bootstrap-x86_64.tar.zst` | ~121 MB | https://geo.mirror.pkgbuild.com/iso/latest/ |

> A partir de 2023 o bootstrap oficial do Arch usa compressão **zstd** (`.tar.zst`). No Android o app descompacta com
> [`zstd-jni`](https://github.com/luben/zstd-jni) + [`commons-compress`](https://commons.apache.org/proper/commons-compress/);
> no desktop o `tar` (Linux) ou Docker (Windows/macOS) resolve.

---

## Estrutura do repositório

```
ArchPC/
├── android/            # App Android (Kotlin + Gradle)
│   └── app/
│       └── src/main/
│           ├── java/com/archpc/app/
│           │   ├── MainActivity.kt          # Home: status, instalar/reinstalar, abrir terminal
│           │   ├── installer/
│           │   │   ├── InstallerActivity.kt # Wizard de instalação com progresso
│           │   │   ├── ArchBootstrap.kt     # Download + extração + primeira config (pacman-key)
│           │   │   ├── ProotProvider.kt     # Localiza/baixa o binário proot (Termux repo)
│           │   │   └── Tarball.kt           # Extrai .tar.zst e .tar.gz com progresso
│           │   ├── terminal/
│           │   │   └── TerminalActivity.kt  # Terminal dentro do Arch (proot)
│           │   └── core/
│           │       ├── Distro.kt            # Caminhos + estado instalado
│           │       ├── Shell.kt             # Spawn de processo + streams
│           │       └── Proot.kt             # Monta os argumentos do proot
│           └── res/                         # Layouts, strings, tema
├── desktop/            # Desktop: Dockerfile + proot nativo + scripts
│   ├── Dockerfile
│   ├── bootstrap.sh    # Baixa o bootstrap do Arch (x86_64)
│   ├── run.sh          # proot nativo, senão Docker (Linux/macOS)
│   ├── run.ps1         # Docker Desktop (Windows)
│   └── README.md
├── scripts/
│   └── fetch-proot.sh  # (opcional) baixa proot do repo Termux para assets do app
├── LICENSE
└── README.md
```

---

## Android — build

Requisitos: [Android Studio](https://developer.android.com/studio) (ou SDK + JDK 17).

1. Abra a pasta `android/` no Android Studio.
2. Sincronize o Gradle (o wrapper baixa o Gradle 8.9 automaticamente).
3. `Run ▶` no device/emulador.

Primeira execução do app:

1. Toque em **Instalar Arch Linux**.
2. O app baixa o tarball da distro (a base limpa) + prepara o motor `proot`.
3. Ao terminar, toque em **Abrir terminal**: você está dentro de um Arch recém-instalado.
4. Rode `pacman -Syu` na primeira vez, depois `pacman -S <o que quiser>`.

> **proot**: o motor não faz parte do Android — o app procura o binário em `filesDir/ng` (você pode mandar via `scripts/fetch-proot.sh` e colocar em `android/app/src/main/assets/ng/`) ou descarrega automaticamente do repositório de pacotes do Termux na primeira instalação.

Para **limpar tudo** (reinstalar do zero): apague o app / toque em Reinstalar. O Arch vai voltar ao estado de fábrica.

---

## Desktop — uso rápido

### Linux / macOS (com `proot` instalado)

```sh
cd desktop
./bootstrap.sh        # baixa o bootstrap oficial (x86_64) e extrai em ./rootfs
./run.sh              # entra no Arch limpo, sem root
```

### Docker (Windows, macOS ou Linux — sem proot)

```sh
cd desktop
docker build -t archpc .   # imagem = Arch Linux base limpo
./run.ps1                  # Windows (usa Docker Desktop)
# ou
./run.sh                   # detecta Docker e cai no modo container
```

Dentro você tem um `pacman` funcional e o sistema 100% limpo para montar seu PC do jeito que quiser.

---

## Por que não é "só mais um chroot"?

- **Reproduzível**: a base é sempre o artefato oficial publicado pela distro — nada modificado.
- **Sem root no Android**: `proot` traduz chamadas do usuário (fake root `-0`), não precisa de `su` nem bootloader desbloqueado.
- **Multiplataforma**: o mesmo conceito de "PC limpo" roda em Android, Windows, macOS e Linux.
- **Reset instantâneo**: apagar o rootfs = "rebaixar o ISO".

## Limitações (honestas)

- As chamadas de sistema não são emuladas como num QEMU: apps com módulos de kernel ou bins que exigem `fakeroot`, mount real ou instruções de outra arquitetura não rodam.
- O terminal do app Android é um streaming stdin/stdout (sem PTY). Para sessão interativa completa com jobs (`fg`, `ctrl+z`) use o app instalando Termux + `proot-distro`, ou use a versão desktop.
- Arch Linux ARM (`aarch64`) é um porte comunitário separado da distro x86_64 oficial; em celulares arm64 usamos o tarball oficial do Arch Linux ARM.

---

## Licença

MIT — veja [`LICENSE`](LICENSE). Erros e ideias: abra uma issue ✦