#Requires -RunAsAdministrator
param(
    [ValidateRange(1, 65535)]
    [int]$Port = 8191
)

$ErrorActionPreference = 'Stop'
$ruleName = "TTRPG Control Deck - Macro Deck TCP $Port (Private LAN)"

Write-Host ''
Write-Host 'TTRPG Control Deck wired-PC connection setup' -ForegroundColor Cyan
Write-Host 'The PC may use Ethernet while TTRPG Control Deck uses Wi-Fi; both must reach the same router/LAN.'
Write-Host 'Do not create an internet/router port-forward for Macro Deck.' -ForegroundColor Yellow
Write-Host ''

$profiles = Get-NetConnectionProfile | Where-Object {
    $_.IPv4Connectivity -ne 'Disconnected' -or $_.IPv6Connectivity -ne 'Disconnected'
}

if (-not $profiles) {
    Write-Warning 'No active Windows network profile was found. Connect Ethernet and run this helper again.'
} else {
    Write-Host 'Active Windows network profiles:' -ForegroundColor Cyan
    $profiles | Format-Table Name, InterfaceAlias, InterfaceIndex, NetworkCategory, IPv4Connectivity -AutoSize
    if (-not ($profiles | Where-Object NetworkCategory -eq 'Private')) {
        Write-Warning 'The active network is not Private. In Windows Settings, open Network & internet > Ethernet and select Private network before pairing.'
    }
}

$existing = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue
if ($existing) {
    $existing | Set-NetFirewallRule -Enabled True -Direction Inbound -Action Allow -Profile Private
    $existing | Get-NetFirewallAddressFilter | Set-NetFirewallAddressFilter -RemoteAddress LocalSubnet
    Write-Host "Updated firewall rule: $ruleName" -ForegroundColor Green
} else {
    New-NetFirewallRule -DisplayName $ruleName -Direction Inbound -Action Allow `
        -Protocol TCP -LocalPort $Port -Profile Private -RemoteAddress LocalSubnet | Out-Null
    Write-Host "Created firewall rule: $ruleName" -ForegroundColor Green
}

Write-Host ''
Write-Host 'Ethernet IPv4 addresses to enter in Macro Deck:' -ForegroundColor Cyan
Get-NetIPAddress -AddressFamily IPv4 -AddressState Preferred | Where-Object {
    $_.IPAddress -notlike '127.*' -and $_.IPAddress -notlike '169.254.*'
} | Select-Object InterfaceAlias, IPAddress | Format-Table -AutoSize

$listener = Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue
if ($listener) {
    Write-Host "Macro Deck is listening on TCP $Port." -ForegroundColor Green
} else {
    Write-Warning "Nothing is listening on TCP $Port yet. Start Macro Deck, select the Ethernet adapter, and keep this port selected."
}

Write-Host ''
Write-Host 'Next: connect TTRPG Control Deck to the router Wi-Fi, then use the Ethernet IPv4 address and this port in the Macro Deck client.'
Write-Host 'If it still fails, leave guest Wi-Fi, disable VPN temporarily, and disable AP/SSID/client isolation in the router.'
Read-Host 'Press Enter to close'
