# JoshHack Agent Guidelines

## Repository layout

- Run Maven commands from `JoshHack/`, the directory containing `pom.xml`.
- Production Java source is in `JoshHack/src/main/java/game/` and `JoshHack/src/main/java/ui/`.
- JUnit tests are in `JoshHack/src/test/java/game/`.
- Do not modify `pom.xml` or quality configuration unless the user explicitly approves it.

## Build and test environment

- Use Java 17.
- Maven is configured by `JoshHack/pom.xml`.
- The test suite uses JUnit 5 and Maven Surefire.
- On Windows, use `.\mvnw.cmd`.
- In Git Bash or CI/Linux, use `./mvnw`.
- The Maven wrapper does not require a separate Maven installation.

Common commands:

```powershell
Set-Location ".\JoshHack"
.\mvnw.cmd test
.\mvnw.cmd '-Dtest=PlayerAiTest' test
.\mvnw.cmd test-compile org.pitest:pitest-maven:mutationCoverage
.\mvnw.cmd spotbugs:spotbugs
```

Before ending implementation work, run at least:

```text
./mvnw test
./mvnw test-compile org.pitest:pitest-maven:mutationCoverage
./mvnw spotbugs:spotbugs
```

When a task targets a class or package, also run the narrow JUnit and PIT targets that cover that scope.

## Required quality checks

The project quality tools are:

- JUnit 5 and Surefire for regression tests.
- PIT for mutation testing.
- JaCoCo when branch or line coverage must be reported.
- SpotBugs for static analysis.

Quality work is complete only when the requested tests, coverage thresholds, and mutation thresholds are demonstrated by command output or generated reports.

Do not weaken, remove, skip, ignore, or disable tests to make a build pass. If a test fails, diagnose and fix the implementation or correct an invalid test while preserving the intended behavior.

## Change discipline

- Preserve existing behavior unless the user explicitly requests a change.
- Make focused changes. Do not refactor unrelated classes or files.
- For the requested scope, change only the relevant production source and test files.
- Report and clearly identify every production-code change for human review.
- Test changes must include meaningful behavior and mutation assertions, not only implementation details or constants.
- Use existing project patterns and JUnit 5 conventions.
- Do not create or modify POM files or quality configuration without explicit approval.

## Git and delivery

- Do not automatically push commits or branches.
- Do not automatically merge or close pull requests.
- Do not rewrite history or create releases unless explicitly requested.
- Summarize changed files, validation commands, results, and known limitations before completion.
