# Route Design - The HAJKEN Car

Video here

## What?
An Android application interacting with a Smartcar. Allows a user to design routes by drawing them on the phone.

## Why?
The interface enables control of vehicle without programming. Can be used for data collection, surveillance of area, automate a path, etc. Many applicable areas where the use of a designed and predetermined route is required.

## How?
### Software:
* Android Studio
* Toasty
* Google Directions API
* Google Location and Activity Recognition
* Google Maps

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


### Android Application

**1. Download repository to desktop or other directory** <br />

**2. Create an API-key for Google Directions API** <br />
*This is required for the application to build.*<br />
* Go to the <a href="https://cloud.google.com/console/google/maps-apis/overview">Google Cloud Platform Console</a> .
* From the Project drop-down menu, select or create the project for which you want to add an API key.
* From the  Navigation menu, select APIs & Services > Credentials.
* On the Credentials page, click Create credentials > API key. <br />
The API key created dialog displays your newly created API key.
* Create a copy of gradle.properties.no.git and name it gradle.properties
* Open gradle.properties
* Find line 15 where it says GoolgeAPIKey = "myKey"
* Insert your API-key where it says "myKey"

**3. Build and run the app in Android Studios** <br />
Tutorial for detailed instructions: https://developer.android.com/studio/run

**4. Give app permission to use location** <br />
Once the application is running, it needs permission to use the phones location.

| <html>  <img src="images/settings.png" class="w3-round" width="250"> </html> |   <html>  <img src="images/settings-appPermissions.png" class="w3-round" width="250"> </html> |   <html>  <img src="images/settings-localPermissions.png" class="w3-round" width="250"> </html> |
 | --- | --- | --- |
| 1. Open Settings. Go to Permissions | 2. Go to Your Location | 3. Enable permission for the app |

**5. The application is ready to run**

## Navigate trough the interface

A link to a video showing how to use the interface
