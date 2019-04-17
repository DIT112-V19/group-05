#include <NewPing.h>
#include <Smartcar.h>

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
int turningSpeed = 55;
int stopSpeed = 0;
boolean obstacleAvoidanceOn = true; //(de)activate obstacle avoidance for testing

//motor pin connection
int leftMotorForwardPin = 8;
int leftMotorBackwardPin = 10;
int leftMotorSpeedPin = 9;
int rightMotorForwardPin = 12;
int rightMotorBackwardPin = 13;
int rightMotorSpeedPin = 11;
//odometer nr1 pin connection
const unsigned short ODOMETER1_PIN = 2;
const unsigned long PULSES_PER_METER = 184; //Calibration, Median of 5 odometer tests

//*create cars' motor's object*
BrushedMotor leftMotor(leftMotorForwardPin, leftMotorBackwardPin, leftMotorSpeedPin);
BrushedMotor rightMotor(rightMotorForwardPin, rightMotorBackwardPin, rightMotorSpeedPin);
//*create car's control object*
DifferentialControl control(leftMotor, rightMotor);
//*create odometer object*
DirectionlessOdometer odometer1(PULSES_PER_METER);
//*create gyroscope object
GY50 gyroscope(gyroOffset);
//*create car object*
SmartCar car(control, gyroscope, odometer1);
//**********

void setup() {
  Serial.begin(9600);
  Serial2.begin(9600); // opens channel for bluetooth, pins 16+17
  Serial2.write("Welcome to HAJKENcar!\nSit back and enjoy the ride.\n "); //Welcome message

  //initialize Odometer
  odometer1.attach(ODOMETER1_PIN, []() {
    odometer1.update();
  });
}

void loop() {

  go();

}

/*
   METHODS
*/

void commands(String commands[], int arraySize) {
  //int arraySize = sizeof(commands) / sizeof(commands[0]); // does not work with long arrays?!
  for (int i = 0; i < (arraySize - 1); i = i + 2) {

    if (commands[i] == "f") {
      forward(commands[i + 1].toInt());
    } else if (commands[i] == "b") {
      backward(commands[i + 1].toInt());
    } else if (commands[i] == "r") {
      rotate(commands[i + 1].toInt());
    } else {
      Serial2.println("unknown or no command");
    }
  }
}

void rotate(int angleToTurn) {
  if (angleToTurn == 0) {
    return; // Dont do anything if angle to turn is 0
  }
  Serial2.println("Car starts to turn");
  int initialHeading = car.getHeading();
  int targetHeading = initialHeading + angleToTurn % 360;

  if (angleToTurn > 0) {
    car.overrideMotorSpeed(turningSpeed, -turningSpeed);
  } else if (angleToTurn < 0) {
    car.overrideMotorSpeed(-turningSpeed, turningSpeed);
  }

  Serial2.print("Initial heading: ");
  Serial2.println(initialHeading);

  int currentTurned = 0;
  do {
    currentTurned = (car.getHeading() - initialHeading) % 360;
    if (angleToTurn < 0  && currentTurned > 0) {
      currentTurned = 360 - currentTurned;
    }

    car.update();

  } while (currentTurned < abs(angleToTurn));
  Serial2.print("Totally turned: ");
  Serial2.println(currentTurned);
  car.setSpeed(stopSpeed);
  car.update();

}

//drives forward up to a set distance
void forward(int distance) {
  Serial2.write("Going forward\n"); //Printing status

  odometer1.reset(); //resets the car's driven distance
  car.setSpeed(speed);
  car.update();

  while (car.getDistance() <= distance) {
    car.update();
    obstacleAvoidance();
    checkForStop();
  }
  car.setSpeed(stopSpeed);
  car.update();
}

//drives backwards up to a set distance
void backward(int distance) {
  Serial2.write("Going backward\n "); //Printing status

  odometer1.reset(); //resets the car's driven distance
  car.setSpeed(-speed);
  car.update();

  while (car.getDistance() <= distance) {
    car.update();
    obstacleAvoidance();
    checkForStop();
  }
  car.setSpeed(stopSpeed);
  car.update();
}

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

void go() {

  String input;
  
  while (true) {
    
    if (Serial.available()) {
      input = Serial.read();
      if (input = 'g') {
        car.setSpeed(speed);
        car.update();
      }
      Serial.print(input);
      if(input = 's'){
        car.setSpeed(0);
        car.update();
      }
    }
  }
}


void checkForStop() {

  char input;
  if (Serial2.available() > 0) {
    input = Serial2.read();
    if (input == 's') {
        car.setSpeed(stopSpeed);
        car.update();
    }
  }
}

//obstacle avoidance - stops in front of obstacle + sends message via bluetooth
void obstacleAvoidance() {
  if (obstacleAvoidanceOn)
  { car.update();
    carDistanceToObstacle = USSensorFront.ping_cm(); // UltraSonicSound Sensor measures (0 = more than 100 cm distance)
    if (carDistanceToObstacle <= stopDistanceToObstacle && carDistanceToObstacle > 0) {
      Serial2.write("Obstacle detected"); // Sending message to bluetooth
      car.setSpeed(stopSpeed);
      car.update();
      while (true) {
        //Stops car
      }
    }
  }
}
