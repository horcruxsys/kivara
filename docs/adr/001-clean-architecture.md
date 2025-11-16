# ADR-001: Adopt Clean Architecture

**Status**: Accepted

**Date**: 2024-11-16

## Context

We needed to establish a scalable and maintainable architecture for the Kivara Android application that would:
- Support multiple developers working in parallel
- Enable easy testing of business logic
- Allow for technology changes without major rewrites
- Provide clear separation of concerns
- Facilitate code reuse across different features

## Decision

We have decided to adopt Clean Architecture with the following layers:

1. **Presentation Layer** (`presentation/`)
   - UI components (Fragments, Activities)
   - ViewModels
   - View state management
   - UI logic only

2. **Domain Layer** (`domain/`)
   - Business logic (Use Cases)
   - Domain models
   - Repository interfaces
   - Framework-independent

3. **Data Layer** (`data/`)
   - Repository implementations
   - Data sources (local and remote)
   - Data models and mappers
   - Framework-specific code

### Dependency Rule
- Presentation depends on Domain
- Data depends on Domain
- Domain depends on nothing (pure Kotlin)

### Implementation Pattern
We will use MVVM (Model-View-ViewModel) pattern in the presentation layer:
- Views observe ViewModels via LiveData/StateFlow
- ViewModels execute Use Cases from the Domain layer
- Use Cases orchestrate data flow from repositories

## Consequences

### Positive
- **Testability**: Business logic can be tested without Android framework dependencies
- **Maintainability**: Clear separation makes code easier to understand and modify
- **Scalability**: New features can be added with minimal impact on existing code
- **Flexibility**: Easy to swap out implementations (e.g., change from REST to GraphQL)
- **Reusability**: Domain layer can be shared across platforms (Android, iOS via KMM)
- **Team Productivity**: Multiple developers can work on different layers simultaneously

### Negative
- **Initial Complexity**: More boilerplate code compared to simpler architectures
- **Learning Curve**: Team members need to understand the architecture principles
- **More Files**: Higher number of classes and interfaces to manage

### Neutral
- **Requires Discipline**: Team must follow the architecture consistently
- **Migration Effort**: Existing code needs to be gradually refactored

## Alternatives Considered

### MVC (Model-View-Controller)
**Rejected**: Activities/Fragments become bloated with both view and controller logic, making testing difficult.

### MVP (Model-View-Presenter)
**Rejected**: While better than MVC, Presenters can still become complex, and the architecture doesn't scale as well for large apps.

### MVI (Model-View-Intent)
**Considered**: Good for complex state management, but adds more complexity than needed for our current requirements. Can be adopted later if needed.

### Simple MVVM without Clean Architecture
**Rejected**: Doesn't provide enough separation for long-term maintainability and testability.

## References

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Clean Architecture Sample](https://github.com/android/architecture-samples)
- [Guide to app architecture](https://developer.android.com/jetpack/guide)
