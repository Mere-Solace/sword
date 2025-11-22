# Architecture Decision Records (ADRs)

This directory contains Architecture Decision Records documenting significant technical decisions made during the development of Sword: Combat Evolved.

## Active ADRs

### Infrastructure & Build

- **[ADR 001: Gradle Wrapper Files in Repository](001-gradle-wrapper.md)**
  - Status: Proposed
  - Date: 2025-10-30
  - Decision: Include Gradle wrapper files in version control for reproducible builds

### API & Libraries

- **[ADR 002: Adventure API Sound Type](002-adventure-api-sound-type.md)**
  - Status: Accepted
  - Date: 2025-11-02
  - Decision: Use Adventure API's `Sound` type instead of raw Bukkit sound enums

### Configuration Architecture

- **[ADR 003: Hybrid Configuration Pattern](003-hybrid-configuration-pattern-DEPRECATED.md)** ⚠️ DEPRECATED
  - Status: **Superseded by ADR 005**
  - Date: 2025-11-03
  - Superseded: 2025-11-19
  - Decision: Flatten simple configs (2-3 values), keep complex configs nested
  - Note: Replaced by static configuration approach (see ADR 005)

- **[ADR 005: Static Configuration Class](005-static-configuration-class.md)** ⭐
  - Status: Accepted (Current)
  - Date: 2025-11-19
  - Decision: Use static Config class with package-private setters and generic loading
  - Supersedes: ADR 003

### State Management

- **[ADR 004: Umbral Blade State Machine](004-umbral-blade-state-machine.md)**
  - Status: Accepted
  - Date: 2025-11-13
  - Decision: Implement state machine pattern for Umbral Blade ability lifecycle

## ADR Format

Each ADR follows this structure:

```text
# ADR XXX: Title

**Status**: Proposed | Accepted | Deprecated | Superseded
**Date**: YYYY-MM-DD
**Authors**: Author names

## Context
What is the issue we're seeing that is motivating this decision?

## Decision
What is the change that we're actually proposing or have agreed to?

## Reasoning
Why are we making this decision? What alternatives did we consider?

## Consequences
What becomes easier or more difficult as a result of this change?
```

## Reading ADRs

- **Start with ADR 005** for current configuration architecture
- **ADR 004** for state machine patterns used in abilities
- **ADR 002** for sound system architecture
- **ADR 001** for build system understanding

## Superseded ADRs

These ADRs are kept for historical reference but are no longer the active approach:

- **[ADR 003: Hybrid Configuration Pattern](003-hybrid-configuration-pattern-DEPRECATED.md)**: Replaced by static configuration (ADR 005) on 2025-11-19

## Related Documentation

- [Configuration Lifecycle](../setup/configuration-lifecycle.md) - How config flows from dev to runtime
- [Configuration Guide](../user-guide/configuration.md) - User-facing config documentation
- [Documentation Standards](../standards/documentation-standards.md) - Code documentation requirements
