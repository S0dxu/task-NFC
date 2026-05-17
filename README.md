# Car NFC Automation Tasker

An Android automation utility built with Flutter and Kotlin that turns on GPS location and turns OFF power settings automatically upon detecting a specific vehicle-mounted NFC tag.

## Problem Statement
Manually enabling high-accuracy location services (GPS) and disabling battery-saving modes every time you enter a vehicle is repetitive and easily forgotten. This results in degraded tracking performance for navigation software or location-sharing applications if the settings are left optimized for power saving.

## Solution
This application automates the process using a physical NFC tag placed inside the vehicle. When the device reads the designated tag, a native background routine instantly executes the following system adjustments:
1. Sets the global location mode to high accuracy (`LOCATION_MODE_HIGH_ACCURACY`).
2. Disables the system-wide low-power/battery-saver mode (`low_power`).
3. Triggers a haptic response and updates the Flutter user interface to confirm execution.

## Features
* **Foreground Service Persistence:** Uses a persistent Android Foreground Service to maintain responsiveness without being terminated by the OS memory manager.
* **Hardware UID Filtering:** The automation logic checks for a specific hardware tag UID (for me is `04:43:55:0C:35:02:89`) to prevent accidental triggers from other NFC assets.
* **Flutter & MethodChannel Integration:** A minimal asynchronous Dart layer handles user interface state representations (`IDLE`, `SUCCESS`, `ERROR`) and allows manual re-execution via a UI action.

## System Permissions & Installation

Modifying global system parameters such as `Settings.Secure` and `Settings.Global` requires elevated permissions that Android restricts from standard third-party applications. To deploy this application successfully, you must manually grant the necessary clearance via the Android Debug Bridge (ADB).

### 1. Clone and Build
Clone the repository, modify the UID with your NFC tag id and compile the application to your target device:
```bash
git clone [https://github.com/S0dxu/task-NFC.git](https://github.com/S0dxu/task-NFC.git)
cd task-NFC
flutter build apk
```

Connect the device to your development environment with USB Debugging enabled, and execute the following command to authorize the application to modify secure system properties:
```bash
adb shell pm grant com.example.task android.permission.WRITE_SECURE_SETTINGS
```