# Agent Instructions

## Build system

This project uses Apache Ant. Do NOT use a system-installed `ant`; use the
`antw` wrapper script in the repo root instead:

```bash
./antw <target>
```

`antw` bootstraps from `bootstrap/ant.jar` and invokes Ant using the JDK 17
found in `~/.jdks/`. This avoids the `SecurityManager` removal breakage in
Java 24+.

## Running tests

The primary test target for day-to-day iteration is:

```bash
./antw test.javacCurrent
```

This runs the full javac transform + idempotency test suite against the JVM
that `antw` uses (JDK 17). It covers:

- `javac-*` tests: delombok the `before/` source file and diff against
  `after-delombok/`
- `idempotent-*` tests: run delombok a second time on the `after-delombok/`
  output and confirm no further changes

A clean run ends with `BUILD SUCCESSFUL`. Any `FAIL` lines in the output
include a unified diff showing expected vs actual.

## Updating expected output

**Always ask the user for confirmation before modifying any file under
`test/transform/resource/after-delombok/`.** These files are the canonical
specification of what delombok should produce; unreviewed edits can silently
lower the quality bar or mask regressions.

When a handler change intentionally alters delombok output, update the
corresponding file in:

```
test/transform/resource/after-delombok/<ClassName>.java
```

Then confirm both the `javac-` and `idempotent-` variants of that test pass.
The idempotency test is the stricter one: running delombok on the
`after-delombok/` file must produce byte-for-byte identical output.

## Key source locations

| Area | Path |
|------|------|
| Lombok annotations | `src/core/lombok/` |
| Javac handlers | `src/core/lombok/javac/handlers/` |
| Delombok pretty-printer | `src/delombok/lombok/delombok/PrettyPrinter.java` |
| Transform test inputs | `test/transform/resource/before/` |
| Transform test expected | `test/transform/resource/after-delombok/` |
| Cross-file test stubs | `test/stubs/` |
