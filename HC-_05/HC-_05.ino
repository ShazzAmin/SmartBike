#include <SoftwareSerial.h>

SoftwareSerial BT( 6, 7);

boolean connected = false;

int state = 5;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinmode(state, INPUT);
  Serial.println("Smart Bike initialized, please connect your device");
  
  while(!connected){
    if(digitalRead(state)==HIGH){
      connected = true;
    }
  }
  
  Serial.println("Device is connected");
  //BT.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
 /* if (BTserial.available())
    {  
        c = BTserial.read();
        Serial.write(c);
    }
 
    // Keep reading from Arduino Serial Monitor input field and send to HC-05
    if (Serial.available())
    {
        c =  Serial.read();
        BTserial.write(c);  
    }*/
}
