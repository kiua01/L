# Cross-Edition Capability Mapping (Draft)

> Maps high-level L concepts to Java Edition and Bedrock Edition.

## Legend
- **Full** — Clean, high-fidelity mapping
- **Partial** — Supported with limitations or polyfills
- **Java-only / Bedrock-only** — Not available on the other side
- **Approx** — Best-effort approximation

## Entities & Components

| L Concept              | Java Edition              | Bedrock Edition                    | Notes |
|------------------------|---------------------------|------------------------------------|-------|
| Health                 | Full                      | Full                               | |
| Movement / speed       | Full                      | Full                               | |
| Attack damage/range    | Full                      | Full / Partial                     | Range semantics differ |
| Inventory              | Full                      | Partial                            | Size & UI differ |
| Custom model / texture | Full                      | Full                               | Asset pipelines differ |
| AI goals               | Full                      | Partial                            | Different AI systems |
| Physics / collision    | Full                      | Partial                            | Engine differences |
| NBT / structured data  | Full                      | Approx (properties / dynamic)      | |
| Mixins / bytecode      | Java-only                 | —                                  | |
| Molang expressions     | —                         | Bedrock-only                       | Exposed via `actual` |

## Blocks & Items

| L Concept              | Java                      | Bedrock                            | Notes |
|------------------------|---------------------------|------------------------------------|-------|
| Basic block            | Full                      | Full                               | |
| Block settings         | Full                      | Partial                            | Hardness, luminance, etc. map well |
| Custom block entity    | Full                      | Partial                            | |
| Item components        | Full                      | Partial                            | |
| Food / durability      | Full                      | Full / Partial                     | |
| Tools & armor          | Full                      | Partial                            | |

## Events

| L Concept              | Java                      | Bedrock                            | Notes |
|------------------------|---------------------------|------------------------------------|-------|
| Lifecycle (start/stop) | Full                      | Full                               | |
| Interact / use         | Full                      | Full                               | |
| Hurt / death           | Full                      | Full                               | |
| Tick (per-entity)      | Full                      | Partial                            | Performance model differs |
| World tick             | Full                      | Limited                            | |
| Packet receive         | Full                      | Different model                    | Script events vs packets |

## Networking

| L Concept              | Java                      | Bedrock                            | Notes |
|------------------------|---------------------------|------------------------------------|-------|
| Custom packets         | Full                      | Partial / different                | Bedrock uses script events & RPCs |
| Client ↔ server sync   | Full                      | Partial                            | |

## Data & Assets

| L Concept              | Java                      | Bedrock                            | Notes |
|------------------------|---------------------------|------------------------------------|-------|
| Recipes                | Full                      | Partial                            | Format differs |
| Loot tables            | Full                      | Partial                            | |
| Tags                   | Full                      | Limited                            | |
| Models / textures      | Full                      | Full                               | Different pack layout |
| Sounds                 | Full                      | Full                               | |
| Particles              | Full                      | Full / Partial                     | |

## Diagnostics Policy

When a feature cannot be fully represented on a target edition, L should:

1. Emit a clear diagnostic (error or warning)
2. Suggest `actual(Edition.X)` overrides when appropriate
3. Offer polyfills only when behavior remains acceptable
4. Never silently drop important behavior

## Recommended Workflow

1. Write common high-level L code
2. Target both editions when the feature set is portable
3. Use `actual(Edition.Java)` / `actual(Edition.Bedrock)` for differences
4. Let the compiler report capability gaps early