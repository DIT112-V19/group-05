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
const unsigned int USS2_MAX_DISTANCE = 100;
//*create ultraSonicSensor Object*
NewPing USSensorFront (USS1_TRIGGER_PIN, USS1_ECHO_PIN, USS1_MAX_DISTANCE);
NewPing USSensorRight (USS2_TRIGGER_PIN, USS2_ECHO_PIN, USS2_MAX_DISTANCE);

const unsigned short ODOMETER2_PIN = 3;
const unsigned long PULSES_PER_METER_2 = 345;

//Gyroscope
const int gyroOffset = 11;
//**********


//**********
//distanceCar
//**********
float speed = 50;
int turningSpeed = 55;
int stopSpeed = 0;

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

//GPS pin connection
static const uint32_t GPSBaud = 9600;

//*********
//LEDs
const int LEDgreen = 31; //Green LED - “ready”
const int LEDyellow = 33; //Yellow LED - “Driving on route”
const int LEDred = 35; //Red LED - “obstacle”

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
  Serial3.begin(9600); // opens channel for bluetooth, pins 16+17

  Serial3.write("Welcome to HAJKENcar!\nSit back and enjoy the ride.\n "); //Welcome message
  Serial.write("Welcome to HAJKENcar!\nSit back and enjoy the ride.\n "); //Welcome message

  //initialize Odometers
  odometer2.attach(ODOMETER2_PIN, []() {
    odometer2.update();
  });


  digitalWrite(LEDgreen, HIGH);

  //waitingForInput();
  //forward(150);
  //stringToArray("<l,10,v,1,r,2,f,60,t,90,f,60>");
}

/*
 *********************************************
     LOOP
 *********************************************
**/



void loop() {
  digitalWrite(LEDgreen, HIGH);
  waitingForInput(); //wait for mode input

  String modeInput = Serial3.readStringUntil('!');

  if (modeInput == "g") {
    gpsFunction();
    waitingForInput();
    getInputString();
  }
  else if (modeInput == "d") {
    waitingForInput();
    getInputString();
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
  digitalWrite(LEDgreen, LOW);
  digitalWrite(LEDyellow, HIGH);

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
        Serial3.println("unknown or no command");
        Serial.println("unknown or no command");
      }
    }
    k++;
    if (roundsToDrive > 0 && k <= roundsToDrive) {
      reverseCommands(commands, arraySize);
      k++;
    }
  } while (k <= roundsToDrive);
  Serial3.println("Done");
  digitalWrite(LEDyellow, LOW);

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
      Serial3.println("unknown or no command");
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

//**FORWARD DRIVING WITH OBSTACLE AVOIDANCE**

void forward(int distance) {
  if (distance == 0) {
    return;
  }
  Serial3.write("Going forward\n"); //Printing status
  Serial.write("Going forward\n"); //Printing status

  distanceReset();
  boolean obstacleBypassed = false;
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



  //*****************
  //Correction for overturn or underturn
  int neededTurn = currentTurned + angleToTurn;

  if (abs(neededTurn) > 2) {
    rotate(neededTurn);
  }


  stop();
}


//**STOPPING**

void stop() {
  car.setSpeed(stopSpeed);
  car.update();
}


//**CORRECTION TO DRIVE IN STRAGIHT LINE**

void directionCorrection(int initialHeading) {

  car.update();

  int currentHeading = car.getHeading();
  int headingOffset = (initialHeading - currentHeading);
  headingOffset = mod(headingOffset, 360);

  /* Serial3.print("Current heading: ");
    Serial3.println(currentHeading);

    Serial3.print("Initial Heading: ");
    Serial3.println(initialHeading);

    Serial3.print("Heading offset: ");
    Serial3.println(headingOffset);
  */
  if (headingOffset == 0) {
    car.overrideMotorSpeed(speed, speed);
  } else if (headingOffset > 180) {
    //Serial3.println("Correcting to the LEFT");
    car.overrideMotorSpeed((speed - 7), (speed + 7));
  } else if (headingOffset < 180) {
    //Serial3.println("Correcting to the RIGHT");
    car.overrideMotorSpeed((speed + 7), (speed - 7));
  }
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
    if (Serial3.available()) {
      inputToGo = Serial3.read();
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

  if (Serial3.available()) {
    inputToStop = Serial3.read();
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

void waitingForInput() {
  while (!Serial3.available()) {
    //Do nothing until Serial2 receives something
  }
}

void getInputString() {

  String input = Serial3.readStringUntil('!');
  stringToArray(input);
}

/*
 *********************************************
     OBSTACLE AVOIDANCE
 *********************************************
**/

//**STOPS IN FRONT OF OBSTACLE**

int obstacleAvoidance() {
  car.update();
  carDistanceToObstacle = USSensorFront.ping_cm(); // UltraSonicSound Sensor measures (0 = more than 100 cm distance)
  if (carDistanceToObstacle <= stopDistanceToObstacle && carDistanceToObstacle > 0) {
    Serial3.write("obstacle"); // Sending message to bluetooth
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
  int currentForward = car.getDistance(); //store distance before obstacle
  int extraDistanceForRotation = 5; //drive 5 cm extra to avoid crashing into obstacle

  //Passing frontside of obstacle
  rotate(-90);// turn
  distanceReset();
  followingRightSide(); //check if obstacle still in the way
  widthObstacle = car.getDistance() + extraDistanceForRotation; //store width of Obstacle
  forwardWithoutObstacleControl(extraDistanceForRotation);

  //Passing left side of obstacle
  rotate(90); //turn
  forwardUntilObstacleRightSide();
  followingRightSide(); //check if obstacle still in the way
  lengthObstacle = car.getDistance() + 15; //store length of Obstacle
  Serial3.println("the obstacle has: ");
  Serial3.println(lengthObstacle);

  forwardWithoutObstacleControl(15);
  Serial3.println("Finished with extra distance");


  //Passing backside of obstacle
  rotate(90); //turn
  forwardWithoutObstacleControl(widthObstacle); //drive along backside, go stored width of obstacle
  rotate(-90);
  distanceReset();
  Serial3.println("continue");
  digitalWrite(LEDred, LOW);
  return (lengthObstacle + currentForward); //return sum of distance before obstacle plus length of obstacle
}

//checkingRightSide method
void followingRightSide() {
  int ZeroCounter = 0;
  const int ZeroCounterMargin = 5;
  int currentDistanceRightSide = USSensorRight.ping_cm();
  int initalDistanceRightSide = currentDistanceRightSide;
  int initialHeading = car.getHeading();
  int updatedHeading;

  car.setSpeed(speed); //drive
  car.update();
  while (ZeroCounter <= ZeroCounterMargin) {
    currentDistanceRightSide = USSensorRight.ping_cm();
    Serial3.println("right side sensor");
    Serial3.println(currentDistanceRightSide);
    if (currentDistanceRightSide > 11) {
      updatedHeading = mod((initialHeading + 90), 360);
      directionCorrection(updatedHeading);
    }
    else if (currentDistanceRightSide < 10) {
      updatedHeading = mod((initialHeading - 90), 360);
      directionCorrection(updatedHeading);
    }
    else {
      directionCorrection(initialHeading);
    }


    if (currentDistanceRightSide == 0) {
      ZeroCounter++;
    }
    else {
      ZeroCounter = 0;
    }
    car.update();
    Serial3.println(currentDistanceRightSide);
  }
}

void forwardUntilObstacleRightSide() {
  int initialHeading = car.getHeading();
  int currentDistanceRightSide = USSensorRight.ping_cm();
  car.setSpeed(speed); //drive
  car.update();

  while ((currentDistanceRightSide > 25) || (currentDistanceRightSide == 0) ) {
    currentDistanceRightSide = USSensorRight.ping_cm();
    directionCorrection(initialHeading);
  }
  Serial3.println("right side is now");
  Serial3.println(currentDistanceRightSide);
}



/*
 *********************************************
     GPS FUNCTION
 *********************************************
**/

void gpsFunction() {


  do {
    while (Serial1.available() > 0 && GPSreceiving) {

      gps.encode(Serial1.read());

      if (gps.location.isUpdated()) {
        lat = gps.location.lat();
        lng = gps.location.lng();

        latitude = String(lat, 6);
        longitude = String(lng, 6);

        Serial3.println(latitude + "*" + longitude);
        //Serial.println("Sending this message to device:" + latitude + "*" + longitude);


        GPSreceiving = false;

      }
    }
  } while (GPSreceiving);
}
