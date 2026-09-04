# Contributing to The Rettington Companion

_🤖 This file was generated completely by AI._

Thanks for considering a contribution! This document covers everything you need to get set up and send a pull
request that's easy to review.

By participating in this project you agree to abide by the [Code of Conduct](.github/CODE_OF_CONDUCT.md).

## Getting set up

- JDK 25 (Temurin recommended), driven through Fabric Loom.
- Build: `./gradlew build`

## Project structure

Package root: `de.rettichlp.therettingtoncompanion`

- `mixin/` — mixins & accessor interfaces into vanilla/Fabric classes (e.g. `ChatScreenMixin`, `HudMixin`). Holds the actual injection
  logic but should stay as thin as possible.
- `services/` — stateful logic classes called from mixins (e.g. `ChatLogService`, `InventoryService`, `WidgetService`). New features
  belong here, not in the mixins themselves.
- `configuration/` — mod configuration.
- `gui/` — custom screens/widgets.
- `integrations/` — integration with other mods (ModMenu, Xaero's Minimap).
- `models/` — plain data classes.
- `utils/` — static helpers (e.g. `ChatUtils`, `ModUtils`).

Mixin configuration lives in `src/main/resources/the-rettington-companion.mixins.json` — every new mixin class must be registered
there.
Minecraft/Loader/Fabric API and dependency versions live in `gradle.properties` — don't hardcode them elsewhere.

## Branching & pull requests

Branches must be named `feature/*`, `bugfix/*`, or `hotfix/*`:

- `feature/*` — anything that isn't a fix.
- `bugfix/*` — fixes for bugs that are not yet on `main`.
- `hotfix/*` — emergency-only: fixing a bug that is already live on `main`.

Merge targets (all merges go through a GitHub pull request):

- `feature/*` → `develop`.
- `bugfix/*` → `develop` or a `hotfix/*` branch.
- Only `develop` or `hotfix/*` may be merged into `main`.

Your pull request title must match the name of the branch you're merging, except for merges into `main`, whose
title must be `Release x.y.z`.

Releases are cut from `main` via the Release GitHub Actions workflow — you don't need to do anything for that as a
contributor.

## Code style

The project has a fixed IntelliJ code style and inspection profile; please stick to it rather than your editor's
defaults.

- [IntelliJ code style](https://gist.github.com/rettichlp/19e2a02631ba1a65cff3e0d53324af9d#file-intellij_code_style-xml)
- [IntelliJ inspection profile](https://gist.github.com/rettichlp/19e2a02631ba1a65cff3e0d53324af9d#file-intellij_inspections-xml)

If you use IntelliJ, importing the project's code style scheme (`intellij_code_style`) and inspection profile (`intellij_inspections`)
will apply all of this automatically.

Lombok is available in the project (`compileOnly`/`annotationProcessor`) — use it for boilerplate (getters/setters/builders etc.) where
it makes sense.

## Testing your changes

`./gradlew build` verifies the project compiles and packages correctly. Automated tests don't cover GUI and in-game behavior, so run the
client with `./gradlew runClient` (or the Fabric Loom `client` run configuration in IntelliJ, picked up after a Gradle sync) and check the
change manually in a world before opening a PR.

## Reporting bugs / requesting features

Open a GitHub issue with as much detail as you can: Minecraft/mod version, steps to reproduce, and what you
expected vs. what happened. For feature requests, describe the use case, not just the desired implementation.
