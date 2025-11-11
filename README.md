# Next2Go Racing App

A modern Android racing application built with Kotlin and Jetpack Compose that displays real-time horse, greyhound, and harness racing information. The app features a clean, accessible interface with category filtering and live countdown timers.


#### **Next2Go App** (Main Configuration)
- **Purpose**: Run the app on device/emulator with debugging
- **Target**: `MainActivity` with full app functionality
- **Features**:
   - Live race data from API
   - Real-time countdown updates (1s intervals)
   - Automatic data refresh (30s intervals)
   - Pull-to-refresh support
   - Category filtering (Horse, Greyhound, Harness)

#### **Install Debug APK**
- **Purpose**: Build and install debug APK without running
- **Command**: `./gradlew :app:installDebug`
- **Use case**: Install on multiple devices simultaneously

#### **Build and Test**
- **Purpose**: Full build with comprehensive testing
- **Commands**: `clean build test`
- **Use case**: CI/CD pipeline or pre-release validation

### 3. **Command Line Options**

#### Quick Development Run
```bash
./gradlew :app:installDebug
adb shell am start -n com.yomi.next2go/.MainActivity
```

#### Build Debug APK
```bash
./gradlew :app:assembleDebug
```

#### Run All Tests
```bash
./gradlew test
```

#### Build Release APK
```bash
./gradlew :app:assembleRelease
```
__________________________________________________________________________________________________
__________________________________________________________________________________________________

## Features

- **Real-time Race Data**: Live race information from racing APIs
- **Category Filtering**: Filter by Horse Racing, Greyhound Racing, or Harness Racing
- **Live Countdown**: Real-time countdown timers for race start times
- **Accessibility Support**: Accessibility features and content descriptions
- **Modern UI**: Clean Material 3 design with dark/light theme support
- **Offline Capability**: Graceful error handling and network state management

## Architecture

This project follows **Clean Architecture** principles with a multi-module structure:

### Module Structure
```
app/                    # Main application module (UI layer)
├── src/main/          # Activities, ViewModels, UI screens
└── src/test/          # UI and ViewModel unit tests

core/
├── common/            # Shared utilities and extensions
├── domain/            # Business logic and entities
├── data/              # Repository implementations
├── network/           # API client and network layer
└── ui/                # Shared UI components and theme
```

### Architectural Patterns
- **MVI (Model-View-Intent)**: Unidirectional data flow for UI interactions
- **Repository Pattern**: Abstraction layer for data sources
- **Use Cases**: Encapsulated business logic
- **Dependency Injection**: Using Hilt for dependency management

### Key Technologies
- **Kotlin**: Modern Android development language
- **Jetpack Compose**: Modern declarative UI toolkit
- **Coroutines & Flow**: Asynchronous programming and reactive streams
- **Hilt**: Dependency injection framework
- **Retrofit**: HTTP client for API communication
- **Moshi**: JSON serialization
- **DateTime**: Kotlinx datetime for time handling

## Setup and Installation

### Prerequisites
- **Android Studio**: Arctic Fox or later
- **JDK**: 17 or higher
- **Android SDK**: API level 24+ (Android 7.0)
- **Gradle**: 8.13 (included with wrapper)

### Installation Steps

1. **Clone the repository**:
   ```bash
   git clone <https://github.com/yomiyusuf/Next2Go>
   cd next2go
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync dependencies**:
   ```bash
   ./gradlew build
   ```

4. **Run the app**:
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - Or use the command line: `./gradlew installDebug`

### Build Variants
- **Debug**: Development build with debugging enabled
- **Release**: Production build with optimizations

## Testing

The project includes comprehensive unit tests covering all layers:

### Running Tests

**All tests**:
```bash
./gradlew test
```

**Specific module tests**:
```bash
./gradlew app:testDebugUnitTest
./gradlew core:domain:testDebugUnitTest
./gradlew core:network:testDebugUnitTest
```

**With coverage**:
```bash
./gradlew testDebugUnitTestCoverage
```

### Test Coverage
- **UI Tests**: Compose UI testing with Robolectric
- **ViewModel Tests**: State management and business logic
- **Repository Tests**: Data layer functionality
- **Use Case Tests**: Business logic validation
- **Network Tests**: API client and error handling

### Testing Technologies
- **JUnit 4**: Unit testing framework
- **MockK**: Mocking library for Kotlin
- **Robolectric**: Android framework simulation
- **Compose Testing**: UI component testing
- **Turbine**: Flow testing utilities

## Development

### Code Style
The project uses:
- **Kotlin Coding Conventions**: Standard Kotlin style guide
- **Spotless**: Automatic code formatting
- **Lint**: Static analysis for code quality

Run code formatting:
```bash
./gradlew spotlessApply
```

Check code quality:
```bash
./gradlew spotlessCheck
./gradlew lint
```

### Git Workflow
- **main**: Production-ready code
- **develop**: Integration branch for features
- **feature/***: Individual feature branches

### Key Commands
```bash
# Clean build
./gradlew clean build

# Run specific tests
./gradlew :app:testDebugUnitTest

# Generate APK
./gradlew assembleDebug

# Install debug build
./gradlew installDebug

# Run all checks
./gradlew check
```

## Design Decisions

### UI/UX Approach
- **Accessibility First**: All components include proper content descriptions
- **Responsive Design**: Adapts to different screen sizes
- **Material Design 3**: Modern Android design system
- **Dark/Light Theme**: Automatic theme switching

### Data Flow
1. **API Layer**: Retrofit client fetches race data
2. **Repository**: Transforms and caches data
3. **Use Cases**: Apply business rules
4. **ViewModel**: Manages UI state
5. **Compose UI**: Renders state changes

### Error Handling
- **Network Errors**: Graceful degradation with retry options
- **User Feedback**: Clear error messages and loading states

## Deployment

### Release Build
```bash
./gradlew assembleRelease
```

## Documentation

- **Architecture**: See `ARCHITECTURE.md` for detailed technical documentation
- **Testing Guide**: See `TESTING.md` for comprehensive testing information