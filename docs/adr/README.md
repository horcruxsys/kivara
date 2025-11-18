# Architecture Decision Records (ADR)

This directory contains Architecture Decision Records (ADRs) for the Kivara project.

## What is an ADR?

An Architecture Decision Record (ADR) is a document that captures an important architectural decision made along with its context and consequences.

## ADR Format

Each ADR should follow this structure:

1. **Title**: Short noun phrase
2. **Status**: Proposed, Accepted, Deprecated, Superseded
3. **Context**: What is the issue we're facing?
4. **Decision**: What did we decide to do?
5. **Consequences**: What are the positive and negative outcomes?

## ADR Template

```markdown
# [ADR Number]. [Title]

**Status**: [Proposed | Accepted | Deprecated | Superseded by ADR-XXX]

**Date**: YYYY-MM-DD

## Context

[Describe the context and problem statement. What is the architectural challenge we are facing?]

## Decision

[Describe the decision that was made and why.]

## Consequences

### Positive
- [List positive outcomes]

### Negative
- [List negative outcomes or tradeoffs]

### Neutral
- [List neutral consequences]

## Alternatives Considered

[List other options that were considered and why they were rejected]

## References

[List any relevant references, links, or documents]
```

## Existing ADRs

- [ADR-001: Adopt Clean Architecture](./001-clean-architecture.md)
- [ADR-002: Use Dagger Hilt for Dependency Injection](./002-hilt-dependency-injection.md)
- [ADR-003: Timber for Logging](./003-timber-logging.md)
- [ADR-004: R8 Code Shrinking and Obfuscation](./004-r8-optimization.md)

## Creating a New ADR

1. Copy the template above
2. Create a new file: `XXX-short-title.md`
3. Fill in all sections
4. Submit for review via Pull Request
5. Update this README with the new ADR
