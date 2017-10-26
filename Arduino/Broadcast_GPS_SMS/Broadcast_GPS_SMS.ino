#include "DFRobot_sim808.h"

#define PHONE_NUMBER "16477721337"

DFRobot_SIM808 sim(&Serial);

void setup()
{
  Serial.begin(9600);

  // DEBUG
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(2500);
  digitalWrite(LED_BUILTIN, LOW);

  while(1)
  {
    int x = sim.init();
    if (x == 3) break;
    char output[64];
    snprintf(output, "Failed to initialize SIM module (error code: %d). Retrying...", x);
    Serial.println(output);
    // DEBUG
    digitalWrite(LED_BUILTIN, HIGH);
    delay(500);
    digitalWrite(LED_BUILTIN, LOW);
  }

  if (sim.attachGPS()) Serial.println("GPS enabled.");
  else Serial.println("Failed to enable GPS.");


  sim.sendSMS(PHONE_NUMBER, "INIT: Device intialized.");
}

void loop()
{
  if (sim.getGPS())
  {
    Serial.println("Sending GPS information.");

    char data[256];
    snprintf(data, "GPS: %hd|%hhd|%hhd|%hhd|%hhd|%hhd|%hhd|%f|%f|%f|%f|%f|%f|%f",
      sim.GPSdata.year, sim.GPSdata.month, sim.GPSdata.day, sim.GPSdata.hour,
      sim.GPSdata.minute, sim.GPSdata.second, sim.GPSdata.centisecond, sim.GPSdata.lat,
      sim.GPSdata.lon, sim.GPSdata.speed_kph, sim.GPSdata.heading, sim.GPSdata.altitude);
    
    sim.sendSMS(PHONE_NUMBER, data);
    
    sim.detachGPS();
  }
  
  delay(5000);
}
