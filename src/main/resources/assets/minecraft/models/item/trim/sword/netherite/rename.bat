@echo off
setlocal enabledelayedexpansion

:: === Set your variables here ===
set "search=diamond"
set "replace=netherite"

:: === Process each matching file ===
for %%f in (*%search%*) do (
    set "filename=%%f"
    set "newname=!filename:%search%=%replace%!"
    ren "%%f" "!newname!"
)

echo Renaming complete.
pause
