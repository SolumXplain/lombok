# `@Alias` — Type Alias for Lombok

## What it does

`@Alias` lets you declare a named alias for an existing type, optionally with annotations attached. The alias is an empty interface annotated with `@Alias`; Lombok rewrites every use site at compile time, replacing the alias with the real type and its annotations.

```java
@Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
interface CompanyId {}

// Source:
CompanyId id = getId();
List<CompanyId> ids = new ArrayList<CompanyId>();

// After Lombok:
@Nullable @Typed(CompanyId.class) String id = getId();
List<@Nullable @Typed(CompanyId.class) String> ids = new ArrayList<@Nullable @Typed(CompanyId.class) String>();
```

The alias interface compiles to a real empty interface; it is harmless but unused at runtime. A `@Typed(OriginalAlias.class)` annotation is always added to the rewritten type so tooling can recover the original alias name.

The alias definition can live in the same file, another source file in the same build, or a pre-compiled jar.

---

## Implemented

### Use-site positions

| Position | Example | Notes |
|---|---|---|
| Local variable type | `CompanyId x = ...` | |
| Method parameter type | `void foo(CompanyId x)` | |
| Field type | `CompanyId field` | |
| Method return type | `CompanyId getX()` | |
| Generic type argument | `List<CompanyId>` | in variables, fields, params, return types |
| Extends/implements with generic | `class Foo extends Bar<CompanyId>` | |
| Constructor type argument (var init) | `new ArrayList<CompanyId>()` as initializer | |
| Constructor type argument (method arg) | `setIds(new ArrayList<CompanyId>(list))` | Mapstruct-generated pattern |
| Explicit type witness on method call | `Collections.<CompanyId>emptyList()` | |
| Cast in return statement | `return (CompanyId) x` | |
| Cast in variable initializer | `CompanyId x = (CompanyId) y` | |

### Alias resolution

Alias definitions are resolved in three places, in order:

1. **Same compilation unit** — fast path, no context required.
2. **Other source files in the same build** — via `compiler.todo` (reflective access to javac internals).
3. **Pre-compiled jar** — via the Javac Elements API, using the import list to resolve the FQN.

### Mapstruct integration

Mapstruct generates `*MapperImpl.java` source files that use alias types from the mapper interfaces. Lombok processes these generated files and rewrites alias types throughout, including the constructor-as-method-argument and type-witness patterns that appear in Mapstruct output.

---

## Not yet implemented

### Use-site positions

| Position | Example | Notes |
|---|---|---|
| Wildcard bound | `List<? extends CompanyId>` | `JCWildcard.bound` not visited in `replaceAliasInTypeArg` |
| Array type | `CompanyId[]` | `JCArrayTypeTree` not handled |
| Cast as method argument | `foo((CompanyId) bar)` | `replaceAliasesInExpr` doesn't recurse into `JCTypeCast` |
| Assignment RHS | `list = new ArrayList<CompanyId>()` | `replaceAliasesInExpr` doesn't handle `JCAssign` |
| Conditional (ternary) in init | `CompanyId x = flag ? a() : b()` | `replaceAliasesInExpr` doesn't handle `JCConditional` |
| Lambda parameter type | `Function<CompanyId, R> f = (CompanyId x) -> ...` | `endVisitMethodArgument` does not fire for lambda params |
| Type parameter bound | `<T extends CompanyId> void foo(T t)` | type parameters not visited |

### Other gaps

- **Eclipse/JDT support** — the handler is javac-only; no Eclipse AST visitor exists.
- **Delombok rendering** — the `@Typed` marker annotation is present in delomboked output but no pretty-printer suppresses it; downstream tools see it verbatim.
