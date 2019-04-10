#include <NewPing.h>
#include <Smartcar.h>

//**********
//ultraSonicSensor
float carDistanceToObstacle; //actual distance to "object"
float stopDistanceToObstacle = 20; //Distance that triggers to stop, in cm

//ultraSonicSensor Pin connection
const int USS1_TRIGGER_PIN = 6; //Trigger Pin
const int USS1_ECHO_PIN = 7; //Echo Pin
const unsigned int USS1_MAX_DISTANCE = 100; //max distance 100 cm of an object
//*create ultraSonicSensor Object*
NewPing USSensorFront (USS1_TRIGGER_PIN, USS1_ECHO_PIN, USS1_MAX_DISTANCE);

//Gyroscope
const int gyroOffset = -30;
//**********


//**********
//distanceCar
//**********
float speed = 50;
int stopSpeed = 0;

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
  //initialize Odometer
  odometer1.attach(ODOMETER1_PIN, []() {
    odometer1.update();
  });
}

void loop() {

  backward(100);

  delay(1000);
  
  backward(100);
  while(true){
    car.update();
  }

}

void forward(int distance){

  odometer1.reset();
  car.setSpeed(speed);
  car.update();
  
  while(car.getDistance() <= distance){
    car.update();
    obstacleAvoidance();
  }
  car.setSpeed(0);
  car.update();
}

void backward(int distance){
 
  odometer1.reset();
  car.setSpeed(-speed);
  car.update();
  
  while(car.getDistance() <= distance){
    car.update();
    obstacleAvoidance();
  }
  car.setSpeed(0);
  car.update();
}

void obstacleAvoidance(){
  car.update(); //control cruise control
  carDistanceToObstacle = USSensorFront.ping_cm(); // UltraSonicSound Sensor measures (0 = more than 100 cm distance)
  if (carDistanceToObstacle <= stopDistanceToObstacle && carDistanceToObstacle > 0) { 
    car.setSpeed(stopSpeed);
  }
}
