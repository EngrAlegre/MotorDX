# MobileDx - Motorcycle Diagnostic App

MobileDx is an Android application for motorcycle diagnostics that integrates with ESP32 hardware to provide real-time monitoring and analysis of motorcycle performance. The app communicates with a Firebase Realtime Database to store and retrieve diagnostic data.

## Features

- **Authentication System**: Secure login/registration with email and password
- **Motorcycle Management**: Add, edit, and manage multiple motorcycles
- **Real-time Diagnostics**: Monitor motorcycle performance data in real-time
- **Historical Data**: View historical diagnostic data
- **Notification System**: Receive alerts about motorcycle issues
- **Profile Management**: Manage user profile and settings

## Architecture

The application follows Clean Architecture principles with three main layers:

1. **Presentation Layer**
   - Activities and Fragments (UI components)
   - ViewModels (UI logic and state management)
   - Adapters and UI utilities

2. **Domain Layer**
   - Use Cases (Business logic)
   - Models (Business entities)
   - Repository Interfaces (Data access abstractions)

3. **Data Layer**
   - Repository Implementations
   - Data Sources (Firebase, local storage)
   - Data Models (Entity classes for storage)

```
┌───────────────────────────────────────────────────────────────┐
│                    Presentation Layer                          │
│                                                               │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐      │
│  │  Activities │     │  Fragments  │     │  ViewModels │      │
│  └─────────────┘     └─────────────┘     └─────────────┘      │
└───────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌───────────────────────────────────────────────────────────────┐
│                     Domain Layer                               │
│                                                               │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐      │
│  │  Use Cases  │     │   Models    │     │ Repository  │      │
│  │             │     │             │     │ Interfaces  │      │
│  └─────────────┘     └─────────────┘     └─────────────┘      │
└───────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌───────────────────────────────────────────────────────────────┐
│                     Data Layer                                 │
│                                                               │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐      │
│  │ Repository  │     │ Data        │     │ Remote      │      │
│  │ Impl        │     │ Entities    │     │ Data Source │      │
│  └─────────────┘     └─────────────┘     └─────────────┘      │
└───────────────────────────────────────────────────────────────┘
```

## ESP32 Integration

The application integrates with ESP32 hardware using Firebase as a communication bridge:

- ESP32 sends diagnostic data to Firebase Realtime Database
- Mobile app subscribes to database updates in real-time
- ESP32 receives commands from the mobile app via Firebase

## Build Instructions

### Prerequisites

- Android Studio Electric Eel (2022.1.1) or newer
- JDK 17
- Android SDK 33 (min SDK 24)
- Google Firebase account with Realtime Database enabled

### Setup Steps

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/mobiledx.git
   ```

2. Open the project in Android Studio

3. Place the `google-services.json` file in the app directory:
   - This file is provided with the project in the `app/` directory
   - If you want to use your own Firebase project, generate a new file from the Firebase console

4. Build the project:
   ```
   ./gradlew build
   ```

5. Run the app on an emulator or physical device:
   ```
   ./gradlew installDebug
   ```

## Testing

The application includes a comprehensive test suite:

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test interactions between components
- **UI Tests**: Test the user interface and user workflows

To run all tests:
```
./gradlew test
```

## Firebase Configuration

The app uses the following Firebase services:

- **Authentication**: For user login and registration
- **Realtime Database**: For storing diagnostic data and communication with ESP32
- **Storage**: For storing user and motorcycle images

Database structure:
```
motor-b1a63/
  ├── users/
  │   └── {userId}/
  │       ├── fullName
  │       ├── email
  │       └── ...
  ├── motorcycles/
  │   └── {motorcycleId}/
  │       ├── userId
  │       ├── make
  │       └── ...
  ├── diagnostics/
  │   └── {deviceId}/
  │       ├── batteryVoltage
  │       ├── engineRPM
  │       └── ...
  └── alerts/
      └── {deviceId}/
          └── {alertId}/
              ├── message
              ├── level
              └── timestamp
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For any inquiries, please contact support@mobiledx.com 