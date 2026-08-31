# Foundry + Roll20 control sets

These layouts are prepared for Macro Deck's Windows host. They deliberately use
only stable, documented keys in the base pages. Final creation/import and pairing
must happen on the computer that will actually host the session.

## Home (5 x 3)

| Foundry | Roll20 | Discord mute | Discord deafen | Push to talk |
|---|---|---|---|---|
| GM session | VTT hotbar | Audio | Browser back | Browser refresh |
| PC volume - | PC mute | PC volume + | Full screen | Escape |

## Foundry Core (5 x 3)

| Action | Key | Action | Key | Action | Key |
|---|---|---|---|---|---|
| Pause game | Space | Focus chat | Shift+C | Actor sheet | C |
| Target token | T | Cycle tokens | Tab | Escape/menu | Escape |
| Undo | Ctrl+Z | Copy | Ctrl+C | Paste | Ctrl+V |
| Delete | Delete | Refresh | F5 | Hard refresh | Ctrl+F5 |
| Zoom in | Page Up | Zoom out | Page Down | Ruler while dragging | R |

Foundry's separate **Hotbar** page maps buttons 1–10 directly to keys 1–0.
Another row maps hotbar pages 1–5 to Alt+1 through Alt+5.

The **Foundry Custom GM** page reserves F13–F24. In Foundry's **Game Settings →
Configure Controls**, these can be assigned without conflicting with normal typing:

| Key | Suggested binding |
|---|---|
| F13 | Next combatant |
| F14 | Previous combatant |
| F15 | Start/end combat |
| F16 | Roll initiative |
| F17 | Toggle combat tracker |
| F18 | Secret/GM roll |
| F19 | Apply damage workflow |
| F20 | Apply healing workflow |
| F21 | Toggle token visibility |
| F22 | Open conditions/statuses |
| F23 | Award Hero Point/Inspiration |
| F24 | Emergency pause / safety tool |

Exact custom actions depend on the Foundry version, game system and enabled
modules, so those twelve bindings are intentionally completed on the host world.

## Roll20 Core (5 x 3)

| Select | Draw | Rectangle | Text | Turn tracker |
|---|---|---|---|---|
| Ctrl+S | Ctrl+F | Ctrl+D | Ctrl+G | Ctrl+U |
| Token layer | GM layer | Map layer | Copy | Paste |
| Ctrl+O | Ctrl+K | Ctrl+M | Ctrl+C | Ctrl+V |
| Undo | Delete | Select all | Token vision | Escape |
| Ctrl+Z | Delete | Ctrl+A | Ctrl+L | Escape |

## Roll20 Advanced (5 x 3)

Enable **My Settings → Keyboard Shortcuts → Use advanced keyboard shortcuts**.

| Map `M` | Tokens `O` | GM `K` | Foreground `.` | Lighting `,` |
|---|---|---|---|---|
| Select `S` | Pan `A` | Turn tracker `Y` | Dice GUI `D` | Pages `P` |
| Sidebar `W` | Chat `C` | Journal `J` | Jukebox `N` | Escape |

## Session and communications

- Discord: toggle mute `Ctrl+Shift+M`, toggle deafen `Ctrl+Shift+D`.
- Browser: refresh `F5`, full screen `F11`, back `Alt+Left`, forward `Alt+Right`.
- Windows audio: volume down, mute and volume up through Macro Deck Windows Utils.
- Foundry and Roll20 buttons should switch Macro Deck profiles and focus the correct
  browser window; select the executable/window title on the host PC.

## Required PC-side pieces

1. Macro Deck Windows host 2.15 or newer.
2. Windows Utils extension for hotkeys, browser control and system audio.
3. The Echo's Macro Deck client paired over the same LAN or USB tunnel.
4. Foundry custom F13–F24 bindings configured in the actual world.

The official key references used for the layout are:

- https://foundryvtt.com/article/controls/
- https://foundryvtt.com/article/keybinds/
- https://help.roll20.net/hc/en-us/articles/360039675393-Hotkeys
- https://help.roll20.net/hc/en-us/articles/360039178974-Advanced-Hotkeys
