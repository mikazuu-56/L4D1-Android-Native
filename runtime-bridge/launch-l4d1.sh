#!/data/data/com.termux/files/usr/bin/bash

export PREFIX=/data/data/com.termux/files/usr
export HOME=/data/data/com.termux/files/home
export PATH="$PREFIX/bin:/system/bin"

export DISPLAY=:0
export WINEPREFIX="$HOME/.wine-l4d1-probe"
export GALLIUM_DRIVER=zink
export MESA_LOADER_DRIVER_OVERRIDE=zink

GAME="$HOME/storage/downloads/Left 4 Dead"
LOG="$HOME/l4d1-launch.log"

{
    echo "===== L4D1 ANDROID LAUNCH ====="
    date

    if [ ! -f "$GAME/left4dead.exe" ]; then
        echo "ERROR: left4dead.exe not found"
        exit 1
    fi

    echo "Game: $GAME"
    echo "Runtime: Hangover Wine"
    echo "Display: $DISPLAY"

    cd "$GAME" || exit 1

    WINEDEBUG=-all timeout 90s \
        hangover-wine "$GAME/left4dead.exe"

    RESULT=$?

    echo "Exit code: $RESULT"
    echo "===== LAUNCH COMPLETE ====="

} > "$LOG" 2>&1
