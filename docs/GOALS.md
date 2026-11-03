# L Language Goals

## Primary Goals

1. **Be the best language for Minecraft Java modding**
   - Cleaner and safer than pure Java
   - More Minecraft-aware than generic Kotlin
   - Excellent interop with existing Fabric / Forge / NeoForge / Quilt ecosystems

2. **Excel at Minecraft launchers**
   - PC launchers with modern UI and strong concurrency
   - Android launchers for Minecraft Java Edition
   - Shared core logic between desktop and mobile

3. **Full Kotlin feature parity**
   - Developers coming from Kotlin should feel at home immediately
   - No major missing language features

4. **Modern & Future-proof**
   - Strong null safety
   - First-class coroutines
   - Multiplatform architecture from day one
   - Good performance characteristics for games

5. **Cross-Edition Portability**
   - Make it practical to port Bedrock Add-ons → Java Edition Mods
   - Make it practical to port Java Edition Mods → Bedrock Add-ons
   - Provide a unified high-level model that can target both ecosystems
   - Clear diagnostics and polyfills when features differ between editions

6. **Modern UI built into the language**
   - First-class support for building beautiful, modern launcher and tool interfaces
   - Declarative, reactive, animation-friendly UI capabilities
   - Excellent desktop + Android support
   - All in the same language — no separate UI language needed

7. **One language, one extension**
   - Everything is written in **L**
   - All source files use the **`.L`** extension
   - Mods, launchers, UI, cross-edition logic — all `.L`

## Non-Goals (for now)

- Competing with general-purpose languages outside the Minecraft / game tooling space
- Replacing the JVM (L targets the JVM primarily)
- Inventing a completely new syntax that breaks Kotlin familiarity
- Perfect 1:1 feature parity between Bedrock and Java (some differences are fundamental)
- A separate UI language (UI is part of L)

## Success Metrics (long term)

- Popular mods written in L
- At least one high-quality public Minecraft launcher using L (including modern UI)
- Android Java Edition launcher(s) built with L
- Successful ports of real content between Bedrock and Java using L
- Active community contributing to the language and Minecraft modules