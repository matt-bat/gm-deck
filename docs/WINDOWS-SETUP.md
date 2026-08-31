# Windows and wired-network setup

GM Deck can use Wi-Fi while the Windows PC uses Ethernet. Both devices must be able to reach the same trusted private LAN; the PC does not need a Wi-Fi adapter.

## Quick connection

1. Install the current Macro Deck Windows server from the [official Macro Deck releases](https://github.com/Macro-Deck-App/Macro-Deck/releases/latest).
2. Connect the Windows PC by Ethernet and GM Deck by Wi-Fi to the same router/LAN.
3. Run `tools\windows\RUN-WIRED-SETUP.cmd` and approve the administrator prompt.
4. In Macro Deck on Windows, select the active Ethernet adapter and its IPv4 address.
5. Keep TCP port `8191` unless Macro Deck is configured for a different port.
6. Connect the Macro Deck Android client to that IPv4 address and approve pairing on Windows.
7. Test one temporary button before creating the complete Foundry or Roll20 control set.

## What the helper changes

`SETUP-MACRO-DECK-FIREWALL.ps1` creates or updates one inbound Windows Firewall rule:

- TCP only.
- Port `8191` by default.
- Private network profile only.
- Remote addresses limited to `LocalSubnet`.

Run a different local port from an elevated PowerShell window only when the Macro Deck server uses that same port:

```powershell
.\tools\windows\SETUP-MACRO-DECK-FIREWALL.ps1 -Port 8192
```

Do not create an internet-facing router port-forward. Normal GM Deck pairing is local-LAN traffic and should not expose Macro Deck to the internet.

## Troubleshooting in the simplest order

1. Confirm Macro Deck is running on Windows.
2. Confirm Windows classifies Ethernet as a **Private network**.
3. Confirm Macro Deck selected the active Ethernet adapter rather than a VPN, virtual, Wi-Fi, or disconnected adapter.
4. Confirm the Windows and Android clients use the same TCP port.
5. Confirm GM Deck is on the main Wi-Fi network, not guest Wi-Fi.
6. Temporarily disconnect a VPN and test again.
7. Disable router settings named **AP isolation**, **SSID isolation**, **wireless isolation**, or **client isolation** when they prevent Wi-Fi devices from reaching Ethernet devices.
8. Re-run the helper and check whether it reports that Macro Deck is listening.

Router menus vary, so consult the router manufacturer's documentation before changing isolation settings. Avoid disabling unrelated firewall or security controls.

## VTT controls

After pairing, use [CONTROL-SETS.md](CONTROL-SETS.md) to build separate Foundry, Roll20, Discord, and Windows pages. Test shortcuts in a spare world/game because Foundry systems, modules, and Roll20 advanced-keyboard settings can change available actions.
