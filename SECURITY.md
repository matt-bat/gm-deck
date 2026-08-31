# Security policy

## Supported version

Security fixes are applied to the latest code on the `main` branch.

## Report a vulnerability

Use GitHub's private vulnerability reporting feature from the repository Security tab. Do not open a public issue when a report could expose private campaign URLs, tokens, pairing codes, local-network details, Android signing material, or a practical code-execution path.

Include:

1. A concise description of the issue.
2. The affected Android/Fire OS, browser, or Windows version.
3. Reproduction steps or a minimal test case.
4. The likely impact and any suggested mitigation.

Please allow reasonable time for assessment and a coordinated fix before public disclosure.

## Operational boundaries

- Keep Macro Deck on a trusted private LAN; do not expose its port through an internet router.
- Treat exported GM Deck configuration as private because it can contain campaign URLs and notes.
- Never share the Android signing key used for installed updates.
- Download companion applications from their official projects and verify published hashes where available.
