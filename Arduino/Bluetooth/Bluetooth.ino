#include <SoftwareSerial.h>

SoftwareSerial BT( 6, 7);

boolean connected = false;
char c = ' ';

int state = 5;
void setup() {
 
  Serial.begin(9600);
  pinMode(state, INPUT);
  Serial.println("Smart Bike initialized, please connect your device");
  
  BT.begin(9600);
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
}
