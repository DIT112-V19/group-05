# group-05

## What?
Software that enables the vehicle to follow a user specified route through the use of a smartphone application

## Why?
An interface that enables control of vehicle without programming that can be used for data collection, surveillance of area, automate a path, etc. Many applicable areas where the use of a designed and predetermined route is required.

## How?
### Software:
* Android Studio
* Arduino IDE
* Libraries:
  * smartcar shield
  * NewPing

### Hardware:
* Arduino Mega
* Arduino Shield
* DC motors
* Bluetooth Module
* Ultrasonicsound Sensor - HC-SR04
* Odometers
* Gyroscope

## Setup and get Started

### Give app permission to use location
The application needs permission to use the phones location.


<!DOCTYPE html>
<html>
<body>
  <div class="w3-card-4" style="width:50%">
    <img src="images/settings.png" class="w3-round" width="250">
    <div class="w3-container">
      <p>1. Open Settings. Go to Permissions</p>
    </div>
  </div>
  <div class="w3-card-4" style="width:50%">
    <img src="images/settings-appPermissions.png" class="w3-round" width="250">
    <div class="w3-container">
      <p>2. Go to Your Location</p>
    </div>
  </div>
  <div class="w3-card-4" style="width:50%">
    <img src="images/settings-localPermissions.png" class="w3-round" width="250">
    <div class="w3-container">
      <p>3. Enable permission for the app</p>
    </div>
  </div>
</body>
</html>
