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

## Setup and get Started!



### Enable the app with permission for location
The application needs permission to use the phones location.

| <html>  <img src="images/settings.png" class="w3-round" width="250"> </html> |   <html>  <img src="images/settings-appPermissions.png" class="w3-round" width="250"> </html> |   <html>  <img src="images/settings-localPermissions.png" class="w3-round" width="250"> </html> |
 | --- | --- | --- |
| 1. Open Settings. Go to Permissions | 2. Go to Your Location | 3. Enable permission for the app |


### Create an API-key for Google Directions API
For the application to build an API-key is required.  

1. Go to the <a href="https://cloud.google.com/console/google/maps-apis/overview">Google Cloud Platform Console</a> .
2. From the Project drop-down menu, select or create the project for which you want to add an API key.
3. From the  Navigation menu, select APIs & Services > Credentials.
4. On the Credentials page, click Create credentials > API key.
The API key created dialog displays your newly created API key.
5. Create a copy of gradle.properties.no.git and name it gradle.properties
6. Open gradle.properties
7. Find line 15 where it says GoolgeAPIKey = "myKey"
8. Insert your API-key where it says "myKey"
