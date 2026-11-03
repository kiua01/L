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
   - Port Bedrock Add-ons → Java Edition Mods
   - Port Java Edition Mods → Bedrock Add-ons
   - Unified high-level model + clear diagnostics

6. **Modern UI built into the language**
   - Beautiful, modern launcher and tool interfaces
   - Declarative, reactive, animation-friendly
   - Desktop + Android

7. **First-class 3D**
   - Very good at 3D for tools, games, launchers, and the web
   - High-level scene, model, camera, light, and material APIs
   - Real-time performance

8. **Websites and 3D websites**
   - Very good for building modern websites
   - First-class support for 3D websites
   - Web source files use the **`.le`** extension

9. **Clear file extensions**
   - **`.L`** — mods, launchers, general logic, desktop/mobile UI
   - **`.le`** — websites and web 3D

## Non-Goals (for now)

- Competing with every general-purpose language outside this domain
- Replacing the JVM as the primary runtime
- Perfect 1:1 feature parity between Bedrock and Java
- A separate UI or web language (both are part of L)

## Success Metrics (long term)

- Popular mods written in L
- High-quality Minecraft launchers (PC + Android) using L
- Successful Bedrock ↔ Java ports
- Real websites and 3D websites built with L (`.le`)
- Active community around the language and its modules