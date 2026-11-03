# L Language Overview

## What is L?
L is a modern programming language with full Kotlin feature parity, designed for:

- Minecraft Java modding
- Minecraft launchers (PC + Android)
- Bedrock ↔ Java content portability
- Modern UI
- Real-time 3D
- Websites and 3D websites

## File Extensions
- **`.L`** — mods, launchers, logic, desktop/mobile UI, tools
- **`.le`** — websites and web 3D

## What makes L different?
1. Minecraft-first DSLs (registries, entities, events, packets, datagen)
2. Cross-edition targeting (Java + Bedrock)
3. UI, 3D, and web as part of the same language family
4. One consistent modern type system and tooling story

## Current Stage
Design / early specification. Compiler and runtimes are not implemented yet.

## Reading Order
1. [README](../README.md)
2. [Goals](GOALS.md)
3. [Features](FEATURES.md)
4. [Specification](SPEC.md)
5. [Type System](TYPE_SYSTEM.md)
6. Domain docs: Minecraft DSL, UI, Web & 3D, Cross-Edition
7. [Roadmap](../ROADMAP.md)

## FAQ

**Is L just Kotlin?**  
No. Kotlin is the baseline. L adds Minecraft, launcher, UI, 3D, web, and cross-edition capabilities.

**Can I use existing Java/Kotlin libraries?**  
Yes. Interop is a core goal.

**Do I need a separate language for UI or web?**  
No. UI is part of L (`.L`). Web uses `.le` but is still L.

**Does cross-edition mean perfect ports?**  
No. Some features are Java-only or Bedrock-only. L makes portable designs easier and gaps explicit.