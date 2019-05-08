#include <NewPing.h>
#include <Smartcar.h>
#include "TinyGPS++.h"
#include <SoftwareSerial.h>

//**********
//ultraSonicSensor
float carDistanceToObstacle; //actual distance to next obstacle, in cm
float stopDistanceToObstacle = 5; //distance that triggers to stop, in cm

//ultraSonicSensor Pin connection
const int USS1_TRIGGER_PIN = 6; //Trigger Pin
const int USS1_ECHO_PIN = 7; //Echo Pin
const unsigned int USS1_MAX_DISTANCE = 100; //max distance of an object to be detected, in cm
//*create ultraSonicSensor Object*
NewPing USSensorFront (USS1_TRIGGER_PIN, USS1_ECHO_PIN, USS1_MAX_DISTANCE);

//Gyroscope
const int gyroOffset = 11;
//**********


//**********
//distanceCar
//**********
float speed = 50;
int turningSpeed = 35;
int stopSpeed = 0;
boolean obstacleAvoidanceOn = true; //(de)activate obstacle avoidance for testing
boolean stopFromDriving; //boolean to stop car

//motor pin connection
int leftMotorForwardPin = 8;
int leftMotorBackwardPin = 10;
int leftMotorSpeedPin = 9;
int rightMotorForwardPin = 12;
int rightMotorBackwardPin = 13;
int rightMotorSpeedPin = 11;
//odometer nr1 pin connection
const unsigned short ODOMETER1_PIN = 2;
const unsigned long PULSES_PER_METER_1 = 184;
const unsigned short ODOMETER2_PIN = 3;
const unsigned long PULSES_PER_METER_2 = 295;

//*create cars' motor's object*
BrushedMotor leftMotor(leftMotorForwardPin, leftMotorBackwardPin, leftMotorSpeedPin);
BrushedMotor rightMotor(rightMotorForwardPin, rightMotorBackwardPin, rightMotorSpeedPin);
//*create car's control object*
DifferentialControl control(leftMotor, rightMotor);
//*create odometer object*
DirectionlessOdometer odometer1(PULSES_PER_METER_1);
DirectionlessOdometer odometer2(PULSES_PER_METER_2);

//*create gyroscope object*
GY50 gyroscope(gyroOffset);
//*create car object*
SmartCar car(control, gyroscope, odometer1, odometer2);
//**********

//**********
//Creating GPS-object
TinyGPSPlus gps;

//GPS variables
double lat;
double lng;
String latitude;
String longitude;
boolean GPS = false;

//GPS pin connection
static const int RXPin = 12, TXPin = 13;
static const uint32_t GPSBaud = 9600;

SoftwareSerial ss(RXPin, TXPin);

//*********

/*
 *********************************************
     SETUP
 *********************************************
**/
void setup() {
  Serial.begin(9600);
  Serial2.begin(9600); // opens channel for bluetooth, pins 16+17
  ss.begin(GPSBaud); //GPS

  Serial2.write("Welcome to HAJKENcar!\nSit back and enjoy the ride.\n "); //Welcome message
  Serial.write("Welcome to HAJKENcar!\nSit back and enjoy the ride.\n "); //Welcome message

  //initialize Odometers
  odometer1.attach(ODOMETER1_PIN, []() {
    odometer1.update();
  });
  odometer2.attach(ODOMETER2_PIN, []() {
    odometer2.update();
  });

  while (!Serial2.available()) {
    //Do nothing until Serial2 receives something
  }
}

/*
 *********************************************
     LOOP
 *********************************************
**/



void loop() {

  String input = Serial2.readStringUntil('!');
  //input = "<l,12,v,3,r,3,f,100,t,90,f,100,t,-90>"; //Test input
  Serial.print(input);// Checking input string in serial monitor
  stringToArray(input);

  while (!Serial2.available()) {

  }

}

/*
 *********************************************
     METHODS
 *********************************************
**/


/*
 *********************************************
     INPUT TO ARRAY
 *********************************************
**/

void stringToArray(String str) {
  //String x = "<l,6,v,1,r,0,f,20,t,30>"; // TEST INPUT

  //Getting size from string for array
  String size;
  int k = 3;

  while (str.charAt(k) != ',') {
    size += str.charAt(k);
    k++;
  }
  int sizeInt = size.toInt();

  //---------------------

  String commandArray[sizeInt];
  int indexArray = 0;
  int i = k + 1; //Starting at correct position in string

  while (str.charAt(i) != '>') {
    if (str.charAt(i) == ',') {
      indexArray++;
    } else {
      commandArray[indexArray] += str.charAt(i);
    }
    i++;
  }

  //TEST PRINT

  for (int k = 0; k < sizeInt; k++) {
    Serial.print(commandArray[k]);
    Serial.print(", ");
  }

  //Running command method for current input
  commands(commandArray, sizeInt);
}

/*
 *********************************************
     DRIVING after ARRAY
 *********************************************
**/

void commands(String commands[], int arraySize) {

  int roundsToDrive = commands[3].toInt();

  //Select speed
  if (commands[1].toInt() == 1) {
    speed = 50;
  } else if (commands[1].toInt() == 2) {
    speed = 70;
  } else if (commands[1].toInt() == 3) {
    speed = 90;
  } 

  int k = 0;
  do {

    for (int i = 4; i < (arraySize - 1); i = i + 2) {

      if (commands[i] == "f") {
        forward((int)commands[i + 1].toFloat());
      } else if (commands[i] == "t") {
        rotate((int)commands[i + 1].toFloat());
      } else {
        Serial2.println("unknown or no command");
        Serial.println("unknown or no command");
      }
    }
    k++;
    if (roundsToDrive > 0 && k < roundsToDrive) {
      reverseCommands(commands, arraySize);
      k++;
    }
  } while (k < roundsToDrive);

}

void reverseCommands(String commands[], int arraySize) {

  rotate(180);//Turn around for back

  for (int i = (arraySize - 2); i >= 4; i = i - 2) {

    if (commands[i] == "f") {
      forward((int)commands[i + 1].toFloat());
    } else if (commands[i] == "t") {
      int reverseTurn = (int)commands[i + 1].toFloat();
      reverseTurn = reverseTurn * -1;
      rotate(reverseTurn);
    } else {
      Serial2.println("unknown or no command");
      Serial.println("unknown or no command");
    }
  }

  rotate(180);//Turn around to be ready for going forward
}


/*
 *********************************************
     BASIC DRIVING COMMANDS
 *********************************************
**/

//**FORWARD DRIVING**

void forward(int distance) {
  Serial2.write("Going forward\n"); //Printing status
  Serial.write("Going forward\n"); //Printing status

  odometer1.reset(); //resets the car's driven distance
  odometer2.reset();

  int initialHeading = car.getHeading(); // get heading to drive in straight line

  car.setSpeed(speed);
  car.update();

  while (car.getDistance() <= distance) {
    car.update();
    obstacleAvoidance();
    directionCorrection(initialHeading);
    checkForStop();
  }
  stop();
}

//**ROTATING**

void rotate(int angleToTurn) {
  if (angleToTurn == 0) {
    return; // Dont do anything if angle to turn is 0
  }

  angleToTurn %= 360;

  //Setting rotation
  if (angleToTurn > 0) {
    car.overrideMotorSpeed(turningSpeed, -turningSpeed);
  } else if (angleToTurn < 0) {
    car.overrideMotorSpeed(-turningSpeed, turningSpeed);
  }

  unsigned int initialHeading = car.getHeading();
  int currentTurned = 0;

  while (abs(currentTurned) < abs(angleToTurn)) {
    car.update();
    int currentHeading = car.getHeading();

    if ((angleToTurn < 0) && (currentHeading > initialHeading)) {
      currentHeading -= 360;
    } else if ((angleToTurn > 0) && (currentHeading < initialHeading)) {
      currentHeading += 360;
    }
    currentTurned = initialHeading - currentHeading;
  }

  stop();
}


//**STOPPING**

void stop() {
  Serial2.write("Car stops\n ");
  car.setSpeed(stopSpeed);
  car.update();
}


//**CORRECTION TO DRIVE IN STRAGIHT LINE**

void directionCorrection(int initialHeading) {

  int currentHeading = car.getHeading();
  int headingOffset = (initialHeading - currentHeading) % 360;

  Serial2.println(headingOffset);

  if (headingOffset == 0) {
    car.overrideMotorSpeed(speed, speed);
  } else if (headingOffset < 180) {
    car.overrideMotorSpeed((speed - 5), speed);
  } else if (headingOffset > 180) {
    car.overrideMotorSpeed(speed, (speed - 5));
  }
  car.update();
}

/*
 *********************************************
     EXAMPLE DRIVING SHAPES
 *********************************************
**/

//drives in a square, starting at lower left corner
void square(int sideLength) {
  for (int i = 0; i < 4; i = i + 1) {
    forward(sideLength);
    rotate(90);
  }
}

void circle() {
  String testStringCircle[] = {"f", "20", "r", "45", "f", "20", "r", "45", "f", "20", "r", "45", "f", "20", "r", "45", "f", "20", "r", "45", "f", "20", "r", "45", "f", "20", "r", "45", "f", "20", "r", "45"};
  int circleArrayLength = 32;
  commands(testStringCircle, circleArrayLength);
}


/*
 *********************************************
     MANUAL CONTROL
 *********************************************
**/

void go() {

  char inputToGo;  //input variable
  while (true) {
    if (Serial2.available()) {
      inputToGo = Serial2.read();
      if (inputToGo = 'g') {
        stopFromDriving = false;
        while (!stopFromDriving) {
          car.setSpeed(speed);
          car.update();
          checkForStop();
        }
        break;
      }
    }
  }
}

void checkForStop() {
  while (Serial.available() > 0) { // empties input buffer
    Serial.read();
  }
  char inputToStop;  //input variable

  if (Serial2.available()) {
    inputToStop = Serial2.read();
    if (inputToStop == 's') {
      stop();
      checkForStart();
    }
  }
}

void checkForStart() {
  while (Serial.available() > 0) { // empties input buffer
    Serial.read();
  }
  char inputToStart;  //input variable
  while (true) {
    if (Serial2.available()) {
      inputToStart = Serial2.read();
      if (inputToStart == 'g') {
        car.setSpeed(speed);
        return;
      }
    }
  }
}


/*
 *********************************************
     OBSTACLE AVOIDANCE
 *********************************************
**/

//**STOPS IN FRONT OF OBSTACLE**

void obstacleAvoidance() {
  if (obstacleAvoidanceOn)
  { car.update();
    carDistanceToObstacle = USSensorFront.ping_cm(); // UltraSonicSound Sensor measures (0 = more than 100 cm distance)
    if (carDistanceToObstacle <= stopDistanceToObstacle && carDistanceToObstacle > 0) {
      Serial2.write("Obstacle detected"); // Sending message to bluetooth
      stop();
      while (true) {
        //Stops car
      }
    }
  }
}

void gpsLoop(String input){
  if (input.equals("g")) {
    GPS == true;
    Serial.print("Got into setting GPS to True");
  }

  while (true) {
    gpsFunction();
  }
}

void gpsFunction() {


  while (ss.available() > 0) {

    gps.encode(ss.read());

    if (gps.location.isUpdated()) {
      lat = gps.location.lat();
      lng = gps.location.lng();

      latitude = String(lat, 6);
      longitude = String(lng, 6);

      Serial2.println(latitude + "*" + longitude);
      //Serial.println("Sending this message to device:" + latitude + "*" + longitude);

    }
  }
}
