# GM Deck

An offline-first tabletop companion that turns a small Android display into a focused control surface for Foundry VTT, Roll20, Pathfinder, D&D, Discord, ambience, dice, initiative, and session tools.

[![UI checks](https://github.com/matt-bat/gm-deck/actions/workflows/ci.yml/badge.svg)](https://github.com/matt-bat/gm-deck/actions/workflows/ci.yml)
[![MIT License](https://img.shields.io/badge/license-MIT-57c785.svg)](LICENSE)
[![Support on Ko-fi](https://img.shields.io/badge/support-Ko--fi-f28c6f.svg)](https://ko-fi.com/matt0bat)

> If GM Deck is useful to you, you can optionally [support ongoing development on Ko-fi](https://ko-fi.com/matt0bat). Support helps maintain this and other public tools, but is never required to use the project.

## Interface preview

| DM command centre | Player dashboard |
|:---:|:---:|
| ![GM Deck DM command centre at 960 by 480](screenshots/gm-deck-3.1-dm-view.png) | ![GM Deck Player dashboard at 960 by 480](screenshots/gm-deck-3.1-player-view.png) |

| Two-tap role switcher | Scene Director |
|:---:|:---:|
| ![GM Deck DM and Player role switcher](screenshots/gm-deck-3.1-role-switcher.png) | ![GM Deck Scene Director presets](screenshots/gm-deck-3.1-scene-director.png) |

## What it does

GM Deck 3.1.0 is designed around the 960×480 Echo Show 5 viewport while remaining usable in a normal Android WebView. Its major features are:

- DM and Player interfaces switchable in two taps.
- Campaign profiles for Foundry VTT, Roll20, D&D 5e, Pathfinder 2e, or mixed tables.
- Editable Scene Director presets combining mode, destination, background ambience, and timer.
- Persistent character HP, resources, roll modifiers, conditions, notes, and turn status.
- Fast d4–d100 rolling, formulas, modifiers, advantage, disadvantage, history, and repeat roll.
- Initiative, rounds, ally/foe markers, current/next turn, and combatant HP.
- Offline one-shot sound effects and native background ambience that can continue behind other apps.
- Session checklist, notes, counters, safety state, scene clock, and GM prompt generators.
- Pathfinder/D&D reference launchers and cached open 5e SRD cards.
- Macro Deck and KDE Connect launchers for PC-companion workflows.

GM Deck is not an official Foundry, Roll20, Amazon, Wizards of the Coast, Paizo, Macro Deck, KDE, or Discord product.

## Project scope

This repository contains the custom GM Deck application. It does not root, unlock, or install a replacement operating system on an Echo Show. Device conversion and APK installation are separate operations whose availability and risk depend on the exact hardware and Fire OS release.

Version 3.1.0 has automated browser-visible validation at 960×480 and a successfully built/signed APK. It has not yet been physically validated on the target Alexa device because that device was unavailable during final testing.

## Quick start for contributors

Prerequisites:

- Node.js 22 or newer for UI validation.
- Java 8-compatible compiler, Android SDK platform 23, Android build tools, `dalvik-exchange`, and `keytool` for APK builds.

Install the test dependency and run the complete UI smoke test:

```bash
npm ci
npx playwright install chromium
npm run test:ui
```

The test opens the app at exactly 960×480, checks DM/Player switching, Player persistence and dice, Scene Director apply/edit/clear behavior, persistent control-strip bounds, and browser errors. It writes four current screenshots to `screenshots/`.

Build the Android APK on a compatible Debian/Ubuntu Android SDK environment:

```bash
bash build.sh
```

The build produces `GM-Deck.apk` locally. On its first run, the script creates a project-local development signing key. The APK and key are deliberately ignored by Git. Do not use that development key for an app-store or production release.

Install a locally built APK on an authorized Android device:

```bash
adb install -r GM-Deck.apk
```

## Windows and VTT setup

- [Windows wired-network setup](docs/WINDOWS-SETUP.md)
- [Foundry, Roll20, Discord, and PC control sets](docs/CONTROL-SETS.md)
- [Optional upgrades](docs/OPTIONALS.md)

A hardwired Windows PC and a Wi-Fi GM Deck can communicate when both reach the same private LAN. Macro Deck normally uses TCP port 8191. Do not expose that port through an internet-facing router port-forward; use the private Windows Firewall rule described in the setup guide.

## Architecture and customization

The Android shell, WebView interface, DM/Player flows, campaign model, Scene Director, combat tracker, dice system, session tools, audio synthesis, ambience service, persistence, responsive layout, test harness, and setup automation are custom GM Deck code.

Third-party pieces are intentionally limited:

- Google Material Design navigation icons.
- The external 5e SRD API used by the optional Library search.
- Launch links and integration points for Foundry, Roll20, Macro Deck, KDE Connect, Discord, and rules/reference sites.

No frontend framework, analytics SDK, advertising library, account system, hosted backend, or remote UI bundle is included. See [third-party notices](THIRD-PARTY-NOTICES.md) for attribution.

## Privacy and security

Campaign settings, character data, notes, combat state, and preferences are stored locally in Android WebView storage. Exported configuration text may contain private campaign URLs and should be handled accordingly.

External rule sites, VTTs, Discord, and the SRD service receive their normal network requests only when opened or searched. GM Deck itself has no account service, telemetry backend, or advertising system.

Report sensitive problems according to [SECURITY.md](SECURITY.md). Please do not publish private server URLs, access tokens, pairing codes, campaign notes, or signing keys in an issue.

## Contributing

Focused bug fixes, accessibility improvements, small-screen layout corrections, documentation, and tabletop workflow improvements are welcome. Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.

## License

GM Deck's original source is available under the [MIT License](LICENSE). Third-party assets and linked services retain their own licenses and terms.
