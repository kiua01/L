# Tooling & CLI (Draft)

## CLI: `l`

```
l new <project>          Create a new L project
l build                  Build current project
l run                    Run (desktop / appropriate target)
l test                   Run tests
l check                  Typecheck without full build
l fmt                    Format .L and .le files
l clean                  Clean build outputs
l targets                List configured targets
```

## Project Layout (Vision)

```
my-mod/
  l.toml                 # or build.gradle.kts plugin
  src/
    commonMain/
      Main.L
    jvmMain/
      ...
  assets/
  build/
```

Web project:

```
my-site/
  l.toml
  src/
    HomePage.le
    ProductViewer3D.le
  public/
  build/
```

## Language Server (LSP)
- Completions, hover, go-to-definition
- Diagnostics for `.L` and `.le`
- Formatting support
- Minecraft / UI / 3D aware assistance where possible

## Formatter
- Opinionated defaults
- Stable, minimal config

## Build Integration
- Gradle plugin as primary JVM/Minecraft path
- Simple `l` CLI for quick projects and web
- Publish JVM artifacts to Maven-compatible repositories

## Editor Support
- VS Code / Cursor extension (LSP)
- IDEA plugin (later)