FROM archlinux:latest

RUN pacman -Syu --noconfirm \
    && pacman -S --noconfirm --needed \
        proot procps-ng wget xz tar coreutils bash \
    && rm -rf /var/cache/pacman/pkg

WORKDIR /opt/archpc

COPY bootstrap.sh run.sh ./


RUN chmod +x bootstrap.sh run.sh && ./bootstrap.sh

ENTRYPOINT ["./run.sh"]
