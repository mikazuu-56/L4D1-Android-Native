#!/data/data/com.termux/files/usr/bin/bash

export PREFIX=/data/data/com.termux/files/usr
export HOME=/data/data/com.termux/files/home
export PATH="$PREFIX/bin:/system/bin"

export DISPLAY=:0
export WINEPREFIX="$HOME/.wine-l4d1-probe"
export GALLIUM_DRIVER=zink
export MESA_LOADER_DRIVER_OVERRIDE=zink

LOG="$HOME/dx9-test/apk-dx9-test.log"

{
    echo "===== L4D1 APK DX9 TEST ====="
    date

    cd "$HOME/dx9-test" || exit 1

    WINEDEBUG=-all timeout 20s \
        hangover-wine "$PWD/dx9test.exe"

    RESULT=$?

    echo "Exit code: $RESULT"

    if [ -f dx9-result.txt ]; then
        cat dx9-result.txt
    fi

    echo "===== TEST COMPLETE ====="
} > "$LOG" 2>&1
