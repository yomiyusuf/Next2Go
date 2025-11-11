# Architecture Documentation

## Overview

Next2Go follows **Clean Architecture** principles with a clear separation of concerns across multiple modules. The architecture ensures maintainability, testability, and scalability while adhering to modern Android development best practices.

## Architectural Principles

### 1. Clean Architecture Layers

```
┌─────────────────────────────────────┐
│              UI Layer               │  ← app module
├─────────────────────────────────────┤
│           Domain Layer              │  ← core:domain
├─────────────────────────────────────┤
│            Data Layer               │  ← core:data, core:network
└─────────────────────────────────────┘
```

### 2. Dependency Rule
- **Inner layers** (domain) know nothing about outer layers
- **Outer layers** depend on inner layers
- **Abstractions** define contracts between layers

### 3. Separation of Concerns
- **UI Layer**: Presentation logic and user interactions
- **Domain Layer**: Business logic and entities
- **Data Layer**: Data management and external APIs

## Module Structure

### App Module (`app/`)
**Responsibility**: Main application, UI screens, navigation, and presentation logic

```
app/
├── src/main/java/com/yomi/next2go/
│   ├── MainActivity.kt              # Main entry point
│   ├── model/                       # UI-specific data models
│   │   └── RaceDisplayModel.kt      # Display representation of races
│   ├── mapper/                      # Domain to UI model mappers
│   │   └── RaceDisplayModelMapper.kt
│   ├── mvi/                         # MVI pattern implementations
│   │   ├── RaceIntent.kt           # User actions
│   │   ├── RaceUiState.kt          # UI state representation
│   │   └── RaceSideEffect.kt       # One-time UI events
│   ├── viewmodel/                   # ViewModels
│   │   └── RaceViewModel.kt        # Main race screen state management
│   └── ui/screens/                  # Compose UI screens
│       └── RaceScreen.kt           # Main race listing screen
└── src/test/                       # Unit tests for UI layer
```

**Key Patterns:**
- **MVI**: Unidirectional data flow with intents and state
- **State Management**: Single source of truth pattern

### Core:Domain Module (`core/domain/`)
**Responsibility**: Business logic, entities, and use cases

```
core/domain/
├── src/main/java/com/yomi/next2go/core/domain/
│   ├── model/                       # Domain entities
│   │   ├── Race.kt                 # Race entity
│   │   ├── CategoryId.kt           # Race category enumeration
│   │   ├── DataError.kt            # Error representations
│   │   └── CategoryColor.kt        # Category color mapping
│   ├── repository/                  # Repository interfaces
│   │   └── RaceRepository.kt       # Data access contracts
│   ├── usecase/                     # Business logic
│   │   └── GetNextRacesUseCase.kt  # Race fetching business logic
│   ├── formatter/                   # Data formatting
│   │   └── RaceCountdownFormatter.kt # Time formatting
│   ├── timer/                       # Time-related utilities
│   │   └── CountdownTimer.kt       # Race countdown logic
│   └── mvi/                         # MVI state contracts
└── src/test/                       # Domain layer unit tests
```

**Design Principles:**
- **Use Cases**: Single responsibility business operations
- **Entities**: Pure domain models with no framework dependencies
- **Repository Pattern**: Abstract data access
- **Domain Events**: Business rule validation

### Core:Data Module (`core/data/`)
**Responsibility**: Repository implementations and data management

```
core/data/
├── src/main/java/com/yomi/next2go/core/data/
│   └── repository/                  # Repository implementations
│       └── RaceRepositoryImpl.kt   # Data layer repository
└── src/test/                       # Data layer unit tests
```

**Patterns:**
- **Repository Implementation**: Concrete data access logic
- **Data Mapping**: Convert between network and domain models
- **Caching Strategy**: In-memory caching for performance

### Core:Network Module (`core/network/`)
**Responsibility**: API communication and network handling

```
core/network/
├── src/main/java/com/yomi/next2go/core/network/
│   ├── api/                         # API interfaces
│   │   └── RacingApiService.kt     # Retrofit API definitions
│   ├── model/                       # Network DTOs
│   │   └── RaceResponse.kt         # API response models
│   ├── mapper/                      # Network to domain mapping
│   │   └── RaceMapper.kt           # Convert API models to domain
│   └── error/                       # Error handling
│       └── NetworkErrorMapper.kt   # Network error mapping
└── src/test/                       # Network layer unit tests
```

**Technologies:**
- **Retrofit**: HTTP client
- **Moshi**: JSON serialization
- **OkHttp**: Network interceptors and logging

### Core:UI Module (`core/ui/`)
**Responsibility**: Shared UI components and theming

```
core/ui/
├── src/main/java/com/yomi/next2go/core/ui/
│   ├── components/                  # Reusable UI components
│   │   ├── RaceCard.kt             # Race display component
│   │   └── FilterChip.kt           # Category filter component
│   ├── theme/                       # Design system
│   │   ├── Theme.kt                # Material theme setup
│   │   ├── Color.kt                # Color definitions
│   │   ├── Typography.kt           # Text styles
│   │   └── Spacing.kt              # Layout spacing
│   └── util/                        # UI utilities
│       └── ColorExtensions.kt      # Color conversion utilities
└── src/test/                       # UI component unit tests
```

**Design System:**
- **Material Design 3**: Modern Android design language
- **Accessibility**: Screen reader support and semantic markup
- **Responsive Design**: Adaptive layouts for different screen sizes

### Core:Common Module (`core/common/`)
**Responsibility**: Shared utilities and extensions

```
core/common/
├── src/main/java/com/yomi/next2go/core/common/
│   ├── time/                        # Time utilities
│   │   └── Clock.kt                # Testable time interface
│   └── Extensions.kt               # Kotlin extensions
└── src/test/                       # Common utilities tests
```

## Data Flow Architecture

### 1. User Interaction Flow

```
User Action → Intent → ViewModel → Use Case → Repository → API
                ↓
UI State ← ViewModel ← Domain Model ← Repository ← Network Response
```

### 2. State Management

```kotlin
// MVI Pattern Implementation
sealed class RaceIntent {
    object LoadRaces : RaceIntent()
    object RefreshRaces : RaceIntent()
    data class ToggleCategory(val category: CategoryId) : RaceIntent()
}

data class RaceUiState(
    val races: List<RaceDisplayModel> = emptyList(),
    val selectedCategories: Set<CategoryId> = emptySet(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
```

### 3. Dependency Injection

**Hilt Module Structure:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRaceRepository(
        raceRepositoryImpl: RaceRepositoryImpl
    ): RaceRepository
}
```

## Testing Strategy

### 1. Test Pyramid

```
┌─────────────────┐
│   UI Tests      │  ← Compose UI testing
├─────────────────┤
│ Integration     │  ← Repository tests
├─────────────────┤
│  Unit Tests     │  ← Use cases, ViewModels
└─────────────────┘
```

### 2. Testing Approach

- **Domain Layer**: Pure unit tests with no Android dependencies
- **Data Layer**: Repository tests with mock network responses
- **UI Layer**: Compose testing with Robolectric

### 3. Mock Strategy

```kotlin
// Domain testing with MockK
@Test
fun `execute with category filter returns only matching categories`() = runTest {
    val mockRepository = mockk<RaceRepository> {
        every { getNextToGoRacesStream(10, setOf(CategoryId.HORSE)) } 
            returns flowOf(Result.Success(listOf(horseRace)))
    }
    
    val useCase = GetNextRacesUseCase(mockRepository, mockClock)
    val result = useCase.executeStream(categories = setOf(CategoryId.HORSE)).first()
    
    assertEquals(1, result.data.size)
}
```

## Error Handling

### 1. Error Types

```kotlin
sealed interface DataError {
    object NetworkUnavailable : DataError
    object Timeout : DataError  
    object ServerError : DataError
    data class HttpError(val code: Int, val message: String) : DataError
    data class Unknown(val throwable: Throwable) : DataError
}
```

### 2. Error Propagation

```
Network Exception → NetworkErrorMapper → DataError → Use Case → ViewModel → UI State
```

## Performance Considerations

### 1. Lazy Loading
- **Compose**: Components rendered on-demand
- **Coroutines**: Asynchronous operations for smooth UI

### 2. Memory Management
- **ViewModel**: Survives configuration changes
- **Flow**: Reactive streams with automatic cleanup
- **Lifecycle-aware**: Components respect Android lifecycle

### 3. Network Optimization
- **Caching**: Repository-level caching strategy
- **Error Recovery**: Automatic retry mechanisms
- **Offline Support**: Graceful degradation

## Code Quality

### 1. Static Analysis
- **Detekt**: Kotlin code analysis
- **Spotless**: Code formatting
- **Lint**: Android-specific checks

This architecture ensures maintainable, testable, and scalable code while following Android development best practices and Clean Architecture principles.