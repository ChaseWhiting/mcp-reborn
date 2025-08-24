@echo off
setlocal enabledelayedexpansion

:: === Set your variables here ===
set "search=sword"
set "replace=pickaxe"

:: === Process each matching file ===
for %%f in (*%search%*) do (
    set "filename=%%f"
    set "newname=!filename:%search%=%replace%!"
    ren "%%f" "!newname!"
)

echo Renaming complete.
pause
