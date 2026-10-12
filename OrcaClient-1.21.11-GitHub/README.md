# Orca Client 1.21.11 — GitHub build project

Original Fabric client foundation with an ocean-themed floating ClickGUI, eased scale-in opening animation, ambient bubble particles, draggable panel, categories, and interactive example toggles.

## Build on GitHub
1. Create a GitHub repository and upload the contents of this folder.
2. Open **Actions** and run **Build Orca Client** (or push to `main`).
3. Download the `orca-client-1.21.11` artifact from the completed workflow.
4. Put the resulting mod JAR in the `mods` folder of a Minecraft Java 1.21.11 Fabric installation with Fabric API installed.
5. Open the GUI with **Right Shift**.

## Scope and status
This is a source project, not a precompiled JAR. It has not yet been compiled or tested in-game in this environment because Gradle dependencies cannot be downloaded here. The GitHub Actions build is intended to perform the real compilation and expose any errors.

The GUI's example toggles are UI examples and do not claim to implement Anubis or Goobaa modules. Anubis is marked GPL-3.0-only; any code reused from it must comply with that license. Goobaa's archive declares all-rights-reserved, so its implementation is not copied. Its broad UI ideas are not included as copied code.
