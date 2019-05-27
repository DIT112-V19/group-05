# group-05

## What?
Software that enables the vehicle to follow a user specified route through the use of a smartphone application

## Why?
An interface that enables control of vehicle without programming that can be used for data collection, surveillance of area, automate a path, etc. Many applicable areas where the use of a designed and predetermined route is required.

## How?
### Software:
* Android Studio
* Arduino IDE
* Arduino libraries(*):
  * <a href="https://github.com/platisd/smartcar_shield">smartcar shield</a> by Dimitris Platis
  * <a href="https://playground.arduino.cc/Code/NewPing/">NewPing</a> by Tim Eckel

(*) available via Arduino IDE  

### Hardware:
* Arduino Mega
*  <a href="https://github.com/platisd/smartcar_shield/tree/master/extras/eagle/smartcar_shield">PCB Smart Car Shield</a> 
* 4x DC motors
* Bluetooth module
* Sensors:
	* 2x UltraSonicSound Sensor (HC-SR04)
	* Odometer (speed sensor)
	* Gyroscope
	* GPS-module (Neo-6M)
* [custom-designed PCB (beta) with 3 LEDs (see: pcb/hajken_cutouts/) ]

## Setup and get Started

### Give app permission to use location
The application needs permission to use the phones location.


<!DOCTYPE html>
<html>
<body>
<div class="row">
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
  </div>
</body>
</html>
