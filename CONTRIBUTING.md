# Contributing to GM Deck

Thanks for helping improve GM Deck. Contributions are most useful when they make tabletop controls clearer, faster, more reliable, or more accessible on a small touch display.

## Before opening an issue

1. Search existing issues for the same behavior.
2. Confirm the problem on the latest `main` branch.
3. Remove campaign URLs, tokens, pairing codes, notes, and other private table information.
4. Use private vulnerability reporting for sensitive security problems.

## Local setup

```bash
npm ci
npx playwright install chromium
npm run test:ui
```

The UI test uses a 960×480 viewport and refreshes the screenshots under `screenshots/`.

Android builds additionally require a Java compiler, Android SDK platform 23, Android build tools, `dalvik-exchange`, and `keytool`:

```bash
bash build.sh
```

## Pull request expectations

- Keep the change focused and explain its user-visible result.
- Preserve both DM and Player workflows unless the change explicitly updates both.
- Check the persistent footer, overlays, long labels, and scroll containers at 960×480.
- Keep touch controls readable and provide visible focus states for keyboard users.
- Add or update the smallest browser-visible check for changed interaction behavior.
- Update canonical documentation when setup or behavior changes.
- Do not commit APKs, signing keys, third-party installers, local profiles, or device diagnostics.

By contributing, you agree that your contribution is licensed under the MIT License and that you have the right to submit it.
