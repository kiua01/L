# Multiplatform Targets (Draft)

## Primary Targets

| Target     | Purpose                                      | Typical files |
|------------|----------------------------------------------|---------------|
| `jvm`      | Minecraft Java mods, PC launchers, tools     | `.L`          |
| `android`  | Java Edition launchers on Android, mobile UI | `.L`          |
| `web`      | Websites and 3D websites                     | `.le`         |

## Future Targets
- Native (performance-critical launcher pieces, tools)
- Additional desktop UI backends as needed

## Source Sets (Vision)

```
commonMain/     shared logic
jvmMain/        Java Edition / desktop
androidMain/    Android
webMain/        website & web 3D (.le)
```

## expect / actual
Shared declarations in common code with platform implementations:

```l
expect fun getGameDir(): Path

// jvm
actual fun getGameDir(): Path = ...

// android
actual fun getGameDir(): Path = ...
```

## Edition vs Platform
- **Platform** = runtime target (jvm, android, web)
- **Edition** = Minecraft edition (Java, Bedrock) for content portability

Both dimensions can apply to Minecraft-related projects.