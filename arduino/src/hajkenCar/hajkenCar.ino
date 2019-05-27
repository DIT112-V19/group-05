#include <NewPing.h>
#include <Smartcar.h>
#include "TinyGPS++.h"

//**********
//ultraSonicSensor
float carDistanceToObstacle; //actual distance to next obstacle, in cm
float stopDistanceToObstacle = 5; //distance that triggers to stop, in cm
boolean obstacleBypassOff = false; //deactivate obstacle bypassing - stop driving when obstacle detected
boolean obstacleAvoidanceOn = true; //(de)activate obstacle avoidance for testing
boolean stopFromDriving; //boolean to stop car

//ultraSonicSensor Pin connection
const int USS1_TRIGGER_PIN = 51; //Trigger Pin
const int USS1_ECHO_PIN = 52; //Echo Pin
const int USS2_TRIGGER_PIN = 5; //Trigger Pin
const int USS2_ECHO_PIN = 6; //Echo Pin

const unsigned int USS1_MAX_DISTANCE = 100; //max distance of an object to be detected, in cm
const unsigned int USS2_MAX_DISTANCE = 40;

//*create ultraSonicSensor Object*
NewPing USSensorFront (USS1_TRIGGER_PIN, USS1_ECHO_PIN, USS1_MAX_DISTANCE);
NewPing USSensorRight (USS2_TRIGGER_PIN, USS2_ECHO_PIN, USS2_MAX_DISTANCE);

const unsigned short ODOMETER2_PIN = 2;
const unsigned long PULSES_PER_METER_2 = 345;

//Gyroscope
const int gyroOffset = 20; //updated was 11
//**********


//**********
//Car
//**********
float speed = 50;
const int TURNING_SPEED = 50;
const int STOP_SPEED = 0;
const int SLOW = 50;
const int MEDIUM = 70;
const int FAST = 90;

//motor pin connection
int leftMotorForwardPin = 8;
int leftMotorBackwardPin = 10;
int leftMotorSpeedPin = 9;
int rightMotorForwardPin = 12;
int rightMotorBackwardPin = 13;
int rightMotorSpeedPin = 11;


//*create cars' motor's object*
BrushedMotor leftMotor(leftMotorForwardPin, leftMotorBackwardPin, leftMotorSpeedPin);
BrushedMotor rightMotor(rightMotorForwardPin, rightMotorBackwardPin, rightMotorSpeedPin);
//*create car's control object*
DifferentialControl control(leftMotor, rightMotor);
//*create odometer object*
DirectionlessOdometer odometer2(PULSES_PER_METER_2);

//*create gyroscope object*
GY50 gyroscope(gyroOffset);
//*create car object*
SmartCar car(control, gyroscope, odometer2);

//**********

//**********
//Creating GPS-object
TinyGPSPlus gps;

//GPS variables
double lat;
double lng;
String latitude;
String longitude;
boolean GPSreceiving = true;

//SERIAL setup
static const uint32_t GPSBaud = 9600;
static const uint32_t BluetoothBaud = 9600;
static const uint32_t SerialBaud = 9600;


//*********
//LEDs
const int LEDgreen = 31; //Green LED - “ready”
const int LEDyellow = 33; //Yellow LED - “Driving on route”
const int LEDred = 35; //Red LED - “obstacle”

//Menu control flow
boolean stayInSubMenu = true;

/*
 *********************************************
     SETUP
 *********************************************
**/
void setup() {
  pinMode(LEDyellow, OUTPUT);
  pinMode(LEDgreen, OUTPUT);
  pinMode(LEDred, OUTPUT);

  Serial1.begin(GPSBaud); //GPS
  Serial.begin(9600);
  Serial3.begin(BluetoothBaud); // opens channel for bluetooth, pins 14+15

  initializeOdometer();//initialize odometer
  
  /*-------------------------------------------
  //Tests here:*/ 
  /*--------------------------------------------*/
}

/*
 *********************************************
     LOOP
 *********************************************
**/

void loop() {
  waitingForInput(); //wait for mode input
  stayInSubMenu = true;
  String MenuInput = Serial3.readStringUntil('!');

  if (MenuInput == "m") {
    digitalWrite(LEDgreen, HIGH);
  }

  else if (MenuInput == "g") {
    gpsFunction();
    while (stayInSubMenu) {
      waitingForInput();
      getInputString();
    }
  }
  else if (MenuInput == "d") {
    while (stayInSubMenu) {
      waitingForInput();
      getInputString();
    }
  }
}

/*
 *********************************************
     EXAMPLES
 *********************************************
**/

//input = "<l,12,v,1,r,0,f,100,t,-90,f,50,t,90>"; //Test input
//input = "<l,18,v,1,r,0,f,50,t,90,f,50,t,90,f,50,t,90,f,50,t,90>"; //square

/*
 *********************************************
     INPUT TO ARRAY
 *********************************************
**/

void stringToArray(String str) {

  //Getting the size from the string, for the array
  String size;
  int k = 3;

  while (str.charAt(k) != ',') {
    size += str.charAt(k);
    k++;
  }
  int sizeInt = size.toInt();

  //---------------------

  //Creating the array with the commands
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
  
  //Running command method for current input
  commands(commandArray, sizeInt);
}

/*
 *********************************************
     DRIVING after ARRAY
 *********************************************
**/

void commands(String commands[], int arraySize) {
  
  digitalWrite(LEDgreen, LOW);
  digitalWrite(LEDyellow, HIGH);
  Serial3.println("s");
  int roundsToDrive = commands[3].toInt();

  //-----Select speed---------
  if (commands[1].toInt() == 1) {
    speed = SLOW;
  } else if (commands[1].toInt() == 2) {
    speed = MEDIUM;
  } else if (commands[1].toInt() == 3) {
    speed = FAST;
  }

  //-----Read commands and execute them-----
  int k = 0;
  do {
    for (int i = 4; i < (arraySize - 1); i = i + 2) {

      if (commands[i] == "f") {
        forward((int)commands[i + 1].toFloat());
      } else if (commands[i] == "t") {
        rotate((int)commands[i + 1].toFloat(), 0);
      } else {
        Serial3.println("unknown or no command");
      }
    }
    k++;
    if (roundsToDrive > 0 && k <= roundsToDrive) { //depending on how many times to repeat the route
      reverseCommands(commands, arraySize);
      k++;
    }
  } while (k <= roundsToDrive);
  
  Serial3.println("d");
  digitalWrite(LEDyellow, LOW);
}

void reverseCommands(String commands[], int arraySize) {

  rotate(180, 0); //Turn around before going back on track

  for (int i = (arraySize - 2); i >= 4; i = i - 2) {

    if (commands[i] == "f") {
      forward((int)commands[i + 1].toFloat());
    } else if (commands[i] == "t") {
      int reverseTurn = (int)commands[i + 1].toFloat();
      reverseTurn = reverseTurn * -1;
      rotate(reverseTurn, 0);
    } else {
      Serial3.println("unknown or no command");
    }
  }

  rotate(180, 0); //Turn around to be ready for going forward
}


/*
 *********************************************
     BASIC DRIVING COMMANDS
 *********************************************
**/

//**FORWARD DRIVING WITH OBSTACLE AVOIDANCE**

void forward(int distance) {
  if (distance == 0) {
    return;
  }
 
  boolean obstacleBypassed = false;
  distanceReset();
  car.update();
  int initialHeading = car.getHeading(); // get heading to drive in straight line

  car.setSpeed(speed);
  car.update();
  
  while (car.getDistance() <= distance) {
    car.update();
    distance = distance - obstacleAvoidance(); //if Obstacle, reduce distance by distance before obstacle plus obstacle length
    directionCorrection(initialHeading);
    checkForStop();
  }
  stop();
}

//**FORWARD DRIVING WITHOUT OBSTACLE CONTROL**

void forwardWithoutObstacleControl(int distance) {
  if (distance == 0) {
    return;
  }
  distanceReset();
  int initialHeading = car.getHeading(); // get heading to drive in straight line

  car.setSpeed(speed);
  car.update();
  
  while (car.getDistance() <= distance) {
    car.update();
    directionCorrection(initialHeading);
    checkForStop();
  }
  stop();
}

//**ROTATING**

void rotate(int angleToTurn, int numCorr) {
  if (angleToTurn == 0 || numCorr == 5) {
    return; // Dont do anything if angle to turn is 0
  }

  angleToTurn %= 360;

  //Setting rotation
  if (angleToTurn > 0) {
    car.overrideMotorSpeed(TURNING_SPEED, -TURNING_SPEED);
  } else if (angleToTurn < 0) {
    car.overrideMotorSpeed(-TURNING_SPEED, TURNING_SPEED);
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

  //-----------------------------------
  //Correction for overturn or underturn
  int neededTurn = currentTurned + angleToTurn;

  if (abs(neededTurn) > 2 && abs(neededTurn) < 180) {
    rotate(neededTurn, numCorr + 1);
  }
  stop();
}

//**STOPPING**

void stop() {
  car.setSpeed(STOP_SPEED);
  car.update();
}


//**CORRECTION TO DRIVE IN STRAGIHT LINE**

void directionCorrection(int initialHeading) {

  car.update();

  int currentHeading = car.getHeading();
  int headingOffset = (initialHeading - currentHeading);
  headingOffset = mod(headingOffset, 360);

  if (headingOffset == 0) {
    car.overrideMotorSpeed(speed, speed);
  } else if (headingOffset > 180) {
    car.overrideMotorSpeed((speed - 20), (speed + 20)); //Correcting to the left
  } else if (headingOffset < 180) {
    car.overrideMotorSpeed((speed + 20), (speed - 20)); //Correcting to the right
  }
}

/*
 *********************************************
     MANUAL CONTROL
 *********************************************
**/

void checkForStop() {

  char inputToStop;  //input variable

  if (Serial3.available()) {
    inputToStop = Serial3.read();
    if (inputToStop == 's') {
      stop();
      checkForStart();
    }
  }
}

void checkForStart() {

  char inputToStart;  //input variable
  
  while (true) {
    if (Serial3.available()) {
      inputToStart = Serial3.read();
      if (inputToStart == 'c') {
        car.setSpeed(speed);
        return;
      }
    }
  }
}

/*
 *********************************************
     UTILITY
 *********************************************
**/

int mod( int x, int y ) {
  return x < 0 ? ((x + 1) % y) + y - 1 : x % y;
}

void distanceReset() {
  odometer2.reset();
}

void initializeOdometer() {
  odometer2.attach(ODOMETER2_PIN, []() {
    odometer2.update();
  });
}

void waitingForInput() {
  while (!Serial3.available()) {
    //Do nothing until Serial3 receives something
  }
}

void getInputString() {
  String input = Serial3.readStringUntil('!');

  if (input == "m") {
    stayInSubMenu = false;
    return;
  } else {
    stringToArray(input);
  }
}

/*
 *********************************************
     OBSTACLE AVOIDANCE
 *********************************************
**/
//Hey Hartmut

//**STOPS IN FRONT OF OBSTACLE**

int obstacleAvoidance() {
  car.update();
  carDistanceToObstacle = USSensorFront.ping_cm(); // UltraSonicSound Sensor measures (0 = more than 100 cm distance)
  if (carDistanceToObstacle <= stopDistanceToObstacle && carDistanceToObstacle > 0) {
    Serial3.write("o"); // Sending message to bluetooth
    digitalWrite(LEDred, HIGH);
    stop();
    while (obstacleBypassOff); // BYPASS OBASTACLE DEACTIVATED
    return bypassObstacle();
  }
  else {
    return 0;
  }
}


int bypassObstacle() {
  int widthObstacle;
  int lengthObstacle;
  int adjustedAngle;
  int currentForward = car.getDistance(); //store distance before obstacle
  int headingBeforeObstacle = car.getHeading(); //store current heading
  const int extraDistanceForRotationA = 5; //drive 5 cm extra to avoid crashing into obstacle
  const int extraDistanceForRotationB = 20; //drive 20 cm extra to avoid crashing into obstacle


  //------------Passing frontside of obstacle---------------
  rotate(-90, 0); // turn
  distanceReset();
  followingRightSide(); //check if obstacle still in the way
  widthObstacle = car.getDistance() + extraDistanceForRotationA; //store width of Obstacle
  forwardWithoutObstacleControl(extraDistanceForRotationA);


  //------------Passing left side of obstacle---------------
  rotate(90, 0);
  distanceReset();
  forwardUntilObstacleRightSide();
  followingRightSide(); //check if obstacle still in the way
  lengthObstacle = car.getDistance() + extraDistanceForRotationB; //store length of Obstacle
  forwardWithoutObstacleControl(extraDistanceForRotationB);


  //-----------Passing backside of obstacle-----------------
  rotate(90, 0); //turn
  forwardWithoutObstacleControl(widthObstacle); //drive along backside, go stored width of obstacle
  adjustedAngle = mod((car.getHeading() - headingBeforeObstacle), 360);
  adjustedAngle = adjustedAngle * (-1);
  rotate(adjustedAngle, 0); //turn
  distanceReset();


  Serial3.println("Continue");
  digitalWrite(LEDred, LOW);
  digitalWrite(LEDyellow, HIGH);
  
  return (lengthObstacle + currentForward); //return sum of distance before obstacle plus length of obstacle
}

//checkingRightSide method
void followingRightSide() {
  int zeroCounter = 0;
  const int ZERO_MARGIN = 10;
  int currentDistanceRightSide = USSensorRight.ping_cm();
  int initalDistanceRightSide = currentDistanceRightSide;
  int initialHeading = car.getHeading();
  int updatedHeading;

  car.setSpeed(speed); //drive
  car.update();
  while (zeroCounter <= ZERO_MARGIN) {
    currentDistanceRightSide = USSensorRight.ping_cm();

    if (currentDistanceRightSide > 13) {
      updatedHeading = mod((initialHeading + 90), 360);
      directionCorrection(updatedHeading);
    }
    else if (currentDistanceRightSide < 5) {
      updatedHeading = mod((initialHeading - 90), 360);
      directionCorrection(updatedHeading);
    }
    else {
      directionCorrection(initialHeading);
    }

    if (currentDistanceRightSide == 0) {
      zeroCounter++;
    }
    else {
      zeroCounter = 0;
    }
    car.update();
  }
}

void forwardUntilObstacleRightSide() {
  int initialHeading = car.getHeading();
  int currentDistanceRightSide = USSensorRight.ping_cm();
  int nonZeroCounter = 0;
  const int NON_ZERO_MARGIN = 10;
  car.setSpeed(speed); //drive
  car.update();

  while (nonZeroCounter <= NON_ZERO_MARGIN ) {
    currentDistanceRightSide = USSensorRight.ping_cm();
    directionCorrection(initialHeading);

    if (currentDistanceRightSide > 0) {
      nonZeroCounter++;
    } else {
      nonZeroCounter = 0;
    }
  }
}

/*
 *********************************************
     GPS FUNCTION
 *********************************************
**/

void gpsFunction() {
  unsigned long startMillis = millis();
  unsigned long currentMillis;
  const unsigned long period = 5000;

  do {
    while (Serial1.available() > 0 && GPSreceiving) {
      if ((startMillis + 5000) < millis()) {
        //Searching for gps signal times out
        return;
      }
      gps.encode(Serial1.read());

      if (gps.location.isUpdated()) {
        lat = gps.location.lat();
        lng = gps.location.lng();

        latitude = String(lat, 6);
        longitude = String(lng, 6);

        Serial3.println(latitude + "*" + longitude);

        GPSreceiving = false;
      }
    }
  } while (GPSreceiving);
}
