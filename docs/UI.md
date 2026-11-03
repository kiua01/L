# L UI Module Specification (Draft)

> Status: Early design.  
> UI is a first-class part of L. There is no separate UI language.  
> All files use the **`.L`** extension.

## 1. Goals

- Build **very modern**, beautiful, fluid interfaces
- Excellent support for **desktop** and **Android** (especially Java Edition launchers)
- Declarative, reactive, and animation-friendly
- Seamless integration with the rest of L (launcher logic, Minecraft modules, coroutines, etc.)
- Familiar to developers who know modern declarative UI systems

## 2. Core Concepts

### 2.1 Composable Functions

A composable function describes a piece of UI:

```l
@Composable
fun Greeting(name: String) {
    Text("Hello, $name!")
}
```

Rules (enforced by the type system):
- Only `@Composable` functions may call other `@Composable` functions
- Side-effects that need to run in composition use dedicated effect APIs

### 2.2 Composition & Recomposition

- The runtime builds a composition tree
- When state that a composable reads changes, that composable (and its children as needed) is recomposed
- Goal: fine-grained, efficient updates

### 2.3 State

```l
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    Button(onClick = { count++ }) {
        Text("Clicked $count times")
    }
}
```

Key APIs:
- `mutableStateOf`
- `remember`
- `derivedStateOf`
- `by` delegation for ergonomics

### 2.4 Modifier

Modifiers decorate and configure UI elements:

```l
Text(
    text = "L Launcher",
    modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
)
```

Modifiers are immutable and chainable via `then`.

## 3. Layout System

### 3.1 Basic Layouts

```l
Column { ... }
Row { ... }
Box { ... }
```

### 3.2 Lazy Layouts

```l
LazyColumn { ... }
LazyRow { ... }
LazyVerticalGrid { ... }
```

Essential for instance lists, mod lists, version lists, etc.

### 3.3 Measurement & Constraints

Follow a constraints-based model (similar to modern declarative UI systems) so that custom layouts are possible later.

## 4. Design System / Material-style Components

L ships a default design system suitable for modern launchers:

- `Button`, `TextButton`, `IconButton`
- `Card`, `ElevatedCard`
- `TextField`, `OutlinedTextField`
- `TopAppBar`, `NavigationBar`
- `FloatingActionButton`
- `Dialog`, `DropdownMenu`
- `Switch`, `Checkbox`, `Slider`
- `CircularProgressIndicator`, `LinearProgressIndicator`
- Icons set

Theming support:
```l
MaterialTheme(
    colors = darkColorScheme(...),
    typography = Typography(...),
    shapes = Shapes(...)
) {
    // content
}
```

## 5. Animation

```l
val alpha by animateFloatAsState(if (visible) 1f else 0f)

AnimatedVisibility(visible = visible) {
    // content
}
```

Goals:
- Smooth, high-quality animations out of the box
- Easy transitions for lists and navigation
- Good performance on both desktop and Android

## 6. Platform Integration

### 6.1 Desktop
- Window management
- Menu bars
- System tray (later)
- Native file dialogs
- Keyboard / mouse input

### 6.2 Android
- Activity integration
- Back handling
- Permissions helpers
- System bars / edge-to-edge
- Touch input

Shared business logic stays in common L code; only platform entry points differ.

## 7. Integration with Launcher Logic

Because UI is part of L, calling launcher code is natural:

```l
@Composable
fun HomeScreen(viewModel: LauncherViewModel) {
    val instances by viewModel.instances.collectAsState()

    LazyVerticalGrid(...) {
        items(instances) { instance ->
            InstanceCard(
                instance = instance,
                onClick = { viewModel.launch(instance) }
            )
        }
    }
}
```

`LauncherViewModel` can be ordinary L code using coroutines, flows, and the `l.launcher` module.

## 8. File Organization Example

```
src/
  commonMain/
    HomeScreen.L
    InstanceCard.L
    LauncherViewModel.L
  desktopMain/
    Main.L                 // window entry
  androidMain/
    MainActivity.L         // activity entry
```

All files still end with **`.L`**.

## 9. Runtime Options (Open Decision)

Two main approaches under consideration:

1. **Bind to an existing high-quality UI runtime**  
   (e.g. Compose Multiplatform / Skia) and provide L syntax + type checking on top.

2. **Implement a dedicated L UI runtime**  
   More control, more work.

Early prototypes will likely bind to an existing runtime to move faster, while keeping the L API clean so the backend can evolve.

## 10. Error Messages (Examples)

- “Composable functions can only be called from other composable functions”
- “State was read during composition but is being modified in a non-composable context”
- “Missing platform actual for UI entry point”

## 11. Open Questions

- Exact relationship with Compose Multiplatform
- Degree of custom layout API exposure in v1
- Hot-reload strategy for desktop and Android
- Accessibility support level in the first releases
- How much of the design system is mandatory vs. replaceable

---

This module is one of the key reasons L can power both the **logic** and the **look** of modern Minecraft launchers in a single language.