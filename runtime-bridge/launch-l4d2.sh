#!/data/data/com.termux/files/usr/bin/bash

export PREFIX=/data/data/com.termux/files/usr
export HOME=/data/data/com.termux/files/home
export PATH="$PREFIX/bin:/system/bin"

unset LD_PRELOAD LD_LIBRARY_PATH LD_AUDIT

export DISPLAY=:0
export WINEPREFIX="$HOME/.wine-l4d2-test"
export GALLIUM_DRIVER=zink
export MESA_LOADER_DRIVER_OVERRIDE=zink

GAME="$HOME/l4d2-internal-test"
LOG="$HOME/l4d2-launcher.log"

{
    echo "===== L4D2 ANDROID LAUNCH ====="
    date

    if [ ! -f "$GAME/left4dead2.exe" ]; then
        echo "ERROR: left4dead2.exe not found"
        exit 1
    fi

    if ! command -v hangover-wine >/dev/null 2>&1; then
        echo "ERROR: hangover-wine not found"
        exit 1
    fi

    echo "Game: $GAME"
    echo "Runtime: Hangover Wine"
    echo "Display: $DISPLAY"

    cd "$GAME" || exit 1

    WINEDEBUG=-all \
        hangover-wine ./left4dead2.exe \
        -windowed -w 640 -h 480 \
        -novid -nojoy \
        +fps_max 30

    RESULT=$?

    echo
    echo "===== EXECUTION RESULT ====="
    echo "Exit code: $RESULT"
    echo "Finished: $(date)"

} > "$LOG" 2>&1
