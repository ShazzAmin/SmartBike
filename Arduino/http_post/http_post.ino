/*
### Connect TCP and send GET request.
1. This example is used to test DFRobot_SIM808 GPS/GPRS/GSM Shield's connect TCP and send GET request.
2. Open the SIM808_TCPConnection example or copy these code to your project
3. Download and dial the function switch to Arduino
4. Open serial helper
*/
#include "DFRobot_sim808.h"
#include <SoftwareSerial.h>

//#define PIN_TX    10
//#define PIN_RX    11
//SoftwareSerial mySerial(PIN_TX,PIN_RX);
//DFRobot_SIM808 sim808(&mySerial);//Connect RX,TX,PWR,

//XML template, change values when needed



DFRobot_SIM808 sim808(&Serial);

int year = sim808.GPSdata.year;
int month = sim808.GPSdata.month;
int day = sim808.GPSdata.day;
int hour = sim808.GPSdata.hour;
int minute = sim808.GPSdata.minute;

//the POST request, change values when needed
char http_post[] = "POST /script HTTP/1.0 Host: danielzhang.ddns.net Content-Type: text/xml  <location><time>(no space)yyyy-mm-dd(space)hh.mm</time><latitude>43.472798</latitude><longitude>-80.539651</longitude></location> "  ;

char buffer[512];


void setup(){
 
  Serial.begin(9600);
  
  //******** Initialize sim808 module *************
  while(!sim808.init()) {
      delay(1000);
      Serial.print("Sim808 init error\r\n");
  }
  delay(3000);  
    
  //*********** Attempt DHCP *******************
  while(!sim808.join(F("cmnet"))) {
      Serial.println("Sim808 join network error");
      delay(2000);
  }

  //************ Successful DHCP ****************
  Serial.print("IP Address is ");
  Serial.println(sim808.getIPAddress());

  //*********** Establish a TCP connection ************
  if(!sim808.connect(TCP,"danielzhang.ddns.net", 80)) {
      Serial.println("Connect error");
  }else{
      Serial.println("Connect success");
  }

  //*********** Send a POST request *****************
  Serial.println("waiting to send..");
  sim808.send(http_post, sizeof(http_post)-1);
  while (true) {
      int ret = sim808.recv(buffer, sizeof(buffer)-1);
      if (ret <= 0){
          Serial.println("fetch over...");
          break; 
      }
      buffer[ret] = '\0';
      Serial.print("Recv: ");
      Serial.print(ret);
      Serial.print(" bytes: ");
      Serial.println(buffer);
      break;
  }

  //************* Close TCP or UDP connections **********
  sim808.close();

  //*** Disconnect wireless connection, Close Moving Scene *******
  sim808.disconnect();
}

void loop(){
 // if(RequiredCondition){
     //update values
     year = sim808.GPSdata.year;
     month = sim808.GPSdata.month;
     day = sim808.GPSdata.day;
     hour = sim808.GPSdata.hour;
     minute = sim808.GPSdata.minute;
 // }

  for(int i = 0; i < sizeof(http_post);i++){
    //change values of http_post[]
  }
   //send POST
  Serial.println("waiting to send..");
  sim808.send(http_post, sizeof(http_post)-1);
  while (true) {
      int ret = sim808.recv(buffer, sizeof(buffer)-1);
      if (ret <= 0){
          Serial.println("fetch over...");
          break; 
      }
      buffer[ret] = '\0';
      Serial.print("Recv: ");
      Serial.print(ret);
      Serial.print(" bytes: ");
      Serial.println(buffer);
      break;
  }
  
}
