
#include "DFRobot_sim808.h"
#include <SoftwareSerial.h>

#define PIN_TX    10
#define PIN_RX    11
SoftwareSerial mySerial(PIN_TX,PIN_RX);
DFRobot_SIM808 sim808(&mySerial);//Connect RX,TX,PWR,

//DFRobot_SIM808 sim808(&Serial);

void setup() {
  mySerial.begin(9600);
 // Serial.begin(9600);

  //******** Initialize sim808 module *************
  while(!sim808.init()) { 
      delay(1000);
      mySerial.print("Sim808 init error\r\n");
  }

  //************* Turn on the GPS power************
  if( sim808.attachGPS())
      mySerial.println("Open the GPS power success");
  else 
      mySerial.println("Open the GPS power failure");
  
}

void loop() {
   //************** Get GPS data *******************
   if (sim808.getGPS()) {
    mySerial.print(sim808.GPSdata.year);
    mySerial.print("/");
    mySerial.print(sim808.GPSdata.month);
    mySerial.print("/");
    mySerial.print(sim808.GPSdata.day);
    mySerial.print(" ");
    mySerial.print(sim808.GPSdata.hour);
    mySerial.print(":");
    mySerial.print(sim808.GPSdata.minute);
    mySerial.print(":");
    mySerial.print(sim808.GPSdata.second);
    mySerial.print(":");
    mySerial.println(sim808.GPSdata.centisecond);
    mySerial.print("latitude :");
    mySerial.println(sim808.GPSdata.lat);
    mySerial.print("longitude :");
    mySerial.println(sim808.GPSdata.lon);
    mySerial.print("speed_kph :");
    mySerial.println(sim808.GPSdata.speed_kph);
    mySerial.print("heading :");
    mySerial.println(sim808.GPSdata.heading);
    mySerial.println();

    //************* Turn off the GPS power ************
    sim808.detachGPS();
  }

}
