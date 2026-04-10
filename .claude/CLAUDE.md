# Dawa-BE Context
> **Boot:** Read `.claude/manifest.md` first.

## Index
### 1. Core (Must Read)
- `core/essential-rules.yaml`: Non-negotiable Rules & Tech Stack
- `memory.md`: Current Context & Status

### 2. Design (Read on Demand)
- `core/system-design.yaml`: Architecture, API, Entity Rules

### 3. References (Lazy Load)
- `references/conventions/coding-style.yaml`: Kotlin idioms, naming, DI basics
- `references/conventions/layers/`: Layer-specific guides
  - `application-layer.md`: UseCase & DTO patterns (Static Factory Method)
- `references/conventions/testing-guide.yaml`: Kotest practices
- `references/data-model/`: Domain Entities

### 4. Docs (Read when writing/reviewing specs)
- `docs/spec-writing-guide.yaml`: Spec document format, output location, writing principles
- `docs/templates/feature-spec.yaml`: Feature spec template
- `features/{domain}/`: Feature specs by domain

## Skills
- `/feature`: New Feature (Immutable Spec)
- `/fix`: Bug Fix (Root Cause Analysis)
- `/refactor`: Code Improvement (No logic change)
- `/docs`: Documentation Maintenance

## Agent Behavior (Protocol)
- **Ask Protocol**: If requirements are ambiguous, risky (e.g., deletion), or deviate from conventions, **STOP and ASK** the user immediately.