#include <DFRobot_sim808.h>
#include <SoftwareSerial.h>

DFRobot_SIM808 sim808(&Serial);
SoftwareSerial BT(3,4);

int state = 5;
boolean connected = false;
char c = ' ';
void setup() {
 
  Serial.begin(9600);
  BT.begin(9600);
  pinMode(state, INPUT);
  Serial.print("IP Address is ");Serial.println("Smart Bike initialized, please connect your device");
  
}

void loop() {
      while(!connected){
        if(digitalRead(state)==HIGH){     
          connected = true;
          Serial.println("Device is connected");
        }
      }
      while(connected){
        if(digitalRead(state)==LOW){
          connected = false;
          Serial.println("Device is not connected");
        }
    
        }
   }
   
      

 
 
 /*if (BT.available())
    {  
        c = BT.read();
        Serial.println(c);
    }
 
    // Keep reading from Arduino Serial Monitor input field and send to HC-05
    if (Serial.available())
    {
        c =  Serial.read();
        BT.write(c);  
    }*/

