$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$targets = @(
    Join-Path $root "android\.gradle",
    Join-Path $root "android\.idea",
    Join-Path $root "android\build",
    Join-Path $root "android\app\build",
    Join-Path $root "backend\node_modules",
    Join-Path $root "backend\dist"
)
foreach ($target in $targets) {
    if (Test-Path $target) {
        Write-Host "Removing $target"
        Remove-Item $target -Recurse -Force
    }
}
Write-Host "Done. Project is cleaned for handoff."
