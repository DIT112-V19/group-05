# Route Design - The HAJKEN Car

Video here

## What?

<i>“You draw, we drive!”</i>

An Android App in which the user can draw a (free-hand) route. The vehicle will drive according to the drawn route and, if requested, reverse and repeat it. Additionally, the vehicle can get its current GPS location and drive according to a track based on a route planned in google maps.

## Why?
The interface enables a simple way of controlling the vehicle without programming. Driving according to certain user-drawn route and reversing/repeating this route, can be used for:
* data collection 
* surveillance of area
* art (painting) 
* etc. 

There are many more applicable areas where there is use of a designed and predetermined route.

## How?
### Software:
#### for App
* Android Studio
* Toasty
* Google Directions API
* Google Location and Activity Recognition
* Google Maps
#### for Arduino
* Arduino IDE
* Arduino libraries:
  * <a href="https://github.com/platisd/smartcar_shield">smartcar shield</a> by Dimitris Platis (*)
  * <a href="https://playground.arduino.cc/Code/NewPing/">NewPing</a> by Tim Eckel (*)
  * <a href="http://arduiniana.org/libraries/tinygpsplus/">TinyGPS++</a> by Mikal Hart
  (*) available via Arduino IDE  

### Hardware:
* Mobile Phone with Android OS and bluetooth
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

### Car / Hardware
1. Follow the <a href="https://www.hackster.io/platisd/getting-started-with-the-smartcar-platform-1648ad">instructions</a> for the hardware setup of the smartcar with differential control (aka driving like a tank) 
2. Connect front UltraSonicSound Sensor to pin 51 (trigger) & pin 52 (echo) [or other equalivent free pins]
3. Connect right-side UltraSonicSound Sensor to pin 5 (trigger) & pin 6 (echo) [or other equalivent free pins] (or plugin into pins marked "Sensor right" on *Hajken PCB*)
4. Connect odometer to pin 3 (additional odometer: connect to pin 2) (or plugin into pins marked "Odometer right"/"Odometer left" on *Hajken PCB*)
5. Connect Bluetooth module to (hardware) serial 3 (pin 15(RX) & 14(TX) - connect RX to TX, TX to RX)
6. Connect GPS module to (hardware) serial 1 (pin 19(RX) & 18(TX) - connect RX to TX, TX to RX)
### Arduino
1. Download the repository
2. Open hajkenCar.ino in Arduino IDE, download the above-mentioned libraries (via Arduino IDE) and install TinyGPS++ manually (library folder must be called "TinyGPS++")
3. Adjust pin setup if necessary. 
4. Calibrate odometer(s) and gyroscope. Use Smartcar Shield sketches for calibration (Odometer: sensors->odometer->FindPulsesPerMeter / Gyroscope: sensors->odometer->GyroscopeCalibration). Update PULSES_PER_METER and GYRO_OFFSET values.
5. Optional: Test Sensors. Smartcar Shield library includes several sketches to test sensors' functionality.
6. Upload sketch to your Arduino Smartcar.

### Android Application

**1. Create an API-key for Google Directions API** <br />
*This is required for the application to build.*<br />
* Go to the <a href="https://cloud.google.com/console/google/maps-apis/overview">Google Cloud Platform Console</a> .
* From the Project drop-down menu, select or create the project for which you want to add an API key.
* From the  Navigation menu, select APIs & Services > Credentials.
* On the Credentials page, click Create credentials > API key. <br />
The API key created dialog displays your newly created API key.
* Open the Android folder
* Create a copy of the file gradle.properties.no.git and name it gradle.properties
* Open gradle.properties
* Find line 15 where it says GoolgeAPIKey = "myKey"
* Insert your API-key where it says "myKey"

**2. Build and run the app in Android Studios** <br />
Tutorial for detailed instructions: https://developer.android.com/studio/run

**3. Give app permission to use location** <br />
Once the application is running, it needs permission to use the phones location.

| <html>  <img src="images/settings.png" class="w3-round" width="250"> </html> |   <html>  <img src="images/settings-appPermissions.png" class="w3-round" width="250"> </html> |   <html>  <img src="images/settings-localPermissions.png" class="w3-round" width="250"> </html> |
 | --- | --- | --- |
| a). Open Settings. Go to Permissions | b). Go to Your Location | c). Enable permission for the app |

**4. The application is ready for using**

## Navigate trough the interface

A link to a video showing how to use the interface
