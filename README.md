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
*  <a href="https://github.com/platisd/smartcar_shield/tree/master/extras/eagle/smartcar_shield">PCB Smartcar Shield</a> 
* 4x DC motors
* Bluetooth module
* Sensors:
	* 2x UltraSonicSound Sensor (HC-SR04)
	* Odometer (speed encoders)
	* Gyroscope
	* GPS-module (Neo-6M)
* BETA: Hajken PCB with 3 LEDs and connections for sensors (see: pcb/hajken_cutouts/)

## Setup and get Started

### Car & Arduino
* Follow the <a href="https://www.hackster.io/platisd/getting-started-with-the-smartcar-platform-1648ad">instructions</a> for the hardware setup of the smartcar with differential control (aka driving like a tank) 
* Connect front UltraSonicSound Sensor to pin 51 (trigger) and pin 52 (echo) [or other equal free pins]
* Connect right-side UltraSonicSound Sensor to pin 5 (trigger) and pin 6 (echo) [or other equal free pins] (or plugin into pins marked "Sensor right" on Hajken PCB)
* Connect odometer to pin 3 (additional second odometer can be connect to pin 2) (or plugin into pins marked "Odometer right"/"Odometer left" on Hajken PCB)
* Connect Bluetooth module to (hardware) serial 3 [pin 15(RX), 14(TX) - connect RX to TX, TX to RX]
* Connect GPS module to (hardware) serial 1 [pin 19(RX), 18(TX) - connect RX to TX, TX to RX]


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
