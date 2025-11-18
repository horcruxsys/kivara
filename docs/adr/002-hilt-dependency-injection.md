# ADR-002: Use Dagger Hilt for Dependency Injection

**Status**: Accepted

**Date**: 2024-11-16

## Context

We needed a dependency injection (DI) solution that would:
- Reduce boilerplate code for DI setup
- Integrate seamlessly with Android components
- Support scoping for different lifecycles (Application, Activity, Fragment, ViewModel)
- Provide compile-time safety
- Scale well as the application grows
- Be actively maintained and well-documented

## Decision

We have decided to use **Dagger Hilt** as our dependency injection framework.

### Why Hilt?
1. **Built on Dagger**: Leverages Dagger's compile-time safety and performance
2. **Android-Specific**: Designed specifically for Android with predefined components and scopes
3. **Less Boilerplate**: Reduces DI setup code compared to vanilla Dagger
4. **Standard Components**: Provides standard components for Android classes (Application, Activity, Fragment, Service, etc.)
5. **ViewModel Integration**: First-class support for ViewModels with `@HiltViewModel`
6. **Google Recommended**: Official recommendation from the Android team

### Implementation Approach
- Use `@HiltAndroidApp` annotation on Application class
- Use `@AndroidEntryPoint` for Activities and Fragments
- Use `@HiltViewModel` for ViewModels
- Define modules with `@Module` and `@InstallIn`
- Use appropriate scopes: `@Singleton`, `@ActivityScoped`, `@FragmentScoped`, `@ViewModelScoped`

## Consequences

### Positive
- **Reduced Boilerplate**: Less code needed compared to manual DI or vanilla Dagger
- **Compile-Time Safety**: Errors caught at compile time, not runtime
- **Better Testing**: Easy to provide test implementations via Hilt test APIs
- **Standardization**: Team follows consistent DI patterns
- **Performance**: Compile-time code generation means no runtime reflection
- **Scoping**: Proper lifecycle management prevents memory leaks
- **Community Support**: Large community and extensive documentation

### Negative
- **Build Time**: Annotation processing increases build time (mitigated by KSP)
- **Learning Curve**: Developers need to understand DI concepts and Hilt-specific annotations
- **Generated Code**: Can be harder to debug since some code is generated

### Neutral
- **Locked to Dagger**: Moving away from Hilt would require significant refactoring
- **Code Generation**: Build time increases slightly but provides compile-time safety

## Alternatives Considered

### Koin
**Rejected**: 
- Runtime DI using Kotlin DSL
- No compile-time verification
- Errors only discovered at runtime
- Though simpler syntax, less safe for production apps

### Manual Dependency Injection
**Rejected**:
- Excessive boilerplate code
- Error-prone for large applications
- Difficult to maintain as app grows
- No automatic lifecycle management

### Vanilla Dagger 2
**Rejected**:
- Requires more boilerplate than Hilt
- Need to manually define components for each Android class
- Hilt is built on top of Dagger anyway, providing same benefits with less code

### Kodein
**Rejected**:
- Less popular in Android community
- Limited Android-specific features
- Smaller community and fewer resources

## Migration Strategy

1. Add Hilt dependencies to Version Catalog
2. Annotate Application class with `@HiltAndroidApp`
3. Create initial modules for common dependencies (AppModule, NetworkModule, DatabaseModule)
4. Gradually migrate existing classes to use constructor injection
5. Annotate Activities, Fragments, and ViewModels as needed
6. Write tests using Hilt test APIs

## References

- [Dagger Hilt Documentation](https://dagger.dev/hilt/)
- [Android Dependency Injection with Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Hilt and Dagger Annotations Cheat Sheet](https://developer.android.com/training/dependency-injection/hilt-cheatsheet)
- [Hilt Testing Guide](https://developer.android.com/training/dependency-injection/hilt-testing)
