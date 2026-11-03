# Diagnostics & Error Handling (Draft)

## Principles
- Precise source locations
- Actionable messages
- Notes and suggestions when helpful
- Domain-aware diagnostics (Minecraft, UI, 3D, web, cross-edition)

## Categories

### Language
- Syntax errors
- Unresolved references
- Type mismatches
- Null-safety violations
- Missing `actual` implementations

### Minecraft
- Invalid or poorly formed identifiers
- Registry conflicts
- Unsupported component on target edition
- Missing packet codec fields
- Datagen inconsistencies

### Cross-Edition
- Feature not available on Bedrock/Java target
- Missing edition-specific `actual`
- Behavior divergence warnings

### UI
- Non-composable called from composable context
- Invalid state writes during composition
- Missing platform UI entry point

### 3D
- Invalid scene graph usage
- Missing camera / light in scene (warnings)
- Unsupported model format

### Web (`.le`)
- Invalid page structure
- Asset path issues
- Browser API misuse

## Severity
- **Error** — must be fixed to compile
- **Warning** — compiles but likely problematic
- **Info / Hint** — optional improvements

## Example Messages
- `Identifier "Example:CoolStone" should be lowercase [minecraft-id]`
- `Component 'inventory' is only partially supported on Bedrock [cross-edition]`
- `Composable function 'HomeScreen' cannot be called from non-composable context [ui]`
- `Missing actual for expect 'getGameDir' on target 'android' [multiplatform]`