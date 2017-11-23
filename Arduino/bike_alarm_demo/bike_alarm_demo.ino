#include <DFRobot_sim808.h>
#include <SoftwareSerial.h>

//#define PIN_TX    10
//#define PIN_RX    11
//SoftwareSerial mySerial(PIN_TX,PIN_RX);

//DFRobot_SIM808 sim808(&mySerial);
DFRobot_SIM808 sim808(&Serial);
SoftwareSerial BT(3,4);

boolean connected = false;
char c = ' ';
//char http_cmd[] = "GET //compare.html HTTP/1.0\r\nHost: danielzhang.ddns.net\r\n\r\n";
//char http_cmd[] = "POST //script HTTP/1.1\r\nHost: danielzhang.ddns.net\r\nAccept:*/*\r\nAccept-Encoding: gzip, deflate\r\nContent-Length: 113\r\nUser-Agent: runscope/0.1\r\n\r\n<location><time>2017-11-23 12.22</time><latitude>43.472798</latitude><longitude>-80.539651</longitude></location>\r\n\r\n";
char http_cmd[] = "POST //script HTTP/1.1\r\nHost: danielzhang.ddns.net\r\nAccept:*/*\r\nAccept-Encoding: gzip, deflate\r\nContent-Length: 113\r\nUser-Agent: runscope/0.1\r\n\r\n<location><time>2017-11-23 12.25</time><latitude>43.470325</latitude><longitude>-80.560906</longitude></location>\r\n\r\n";

char buffer[512];
int state = 5;
const long interval = 2000;
unsigned long previousMillis = 0;

void setup() {
  mySerial.begin(9600);
  Serial.begin(9600);
  BT.begin(9600);
  //pinMode(state, INPUT);
  Serial.println("Smart Bike initialized, please connect your device");

   while(!sim808.init()) {
      delay(1000);
      Serial.print("Sim808 init error\r\n");
  }
  delay(3000);
  /*if( sim808.attachGPS())
      Serial.println("Open the GPS power success");
  else
      Serial.println("Open the GPS power failure");*/

  //Attempt DHCP
  //set apn
  while(!sim808.join(F("rogers-core-appl1.apn"))) {
      Serial.println("Sim808 join network error");
      delay(2000);
  }

  //Successful DHCP
  Serial.print("IP Address is ");
  Serial.println(sim808.getIPAddress());

  //Establish a TCP connection
  if(!sim808.connect(TCP,"danielzhang.ddns.net", 80)) {
      Serial.println("Connect error");
  }else{
      Serial.println("Connect danielzhang.ddns.net success");
  }

  /*if( sim808.attachGPS())
      Serial.println("Open the GPS power success");
  else
      Serial.println("Open the GPS power failure");
*/
}

void loop() {
   //checks if bike companion is paired to smartphone app, sends HTTP POST requests with
  // GPS coordinates with time stamp whenver bike is locked (disconnected from smartphone)
   unsigned long currentMillis = millis();
   
   //Loop to set delay between actions without using delay();
   if (currentMillis - previousMillis >= interval){
      previousMillis = currentMillis;
      //bluetooth is connected
      while(!connected){
        if(digitalRead(state)==HIGH){
          connected = true;
          Serial.println("Device is connected");
        }
      }
      //bluetooth not connected
      while(connected){
        if(digitalRead(state)==LOW){
          connected = false;
          /*  if (sim808.getGPS()) {
            Serial.print(sim808.GPSdata.year);
            Serial.print("/");
            Serial.print(sim808.GPSdata.month);
            Serial.print("/");
            Serial.print(sim808.GPSdata.day);
            Serial.print(" ");
            Serial.print(sim808.GPSdata.hour);
            Serial.print(":");
            Serial.print(sim808.GPSdata.minute);
            Serial.print(":");
            Serial.print(sim808.GPSdata.second);
            Serial.print(":");
            Serial.println(sim808.GPSdata.centisecond);
            Serial.print("latitude :");
            Serial.println(sim808.GPSdata.lat);
            Serial.print("longitude :");
            Serial.println(sim808.GPSdata.lon);
            Serial.print("speed_kph :");
            Serial.println(sim808.GPSdata.speed_kph);
            Serial.print("heading :");
            Serial.println(sim808.GPSdata.heading);
            Serial.println();


            // sim808.detachGPS();*/
          Serial.println("Device is not connected");
        }


        //http post request
      Serial.println("waiting to post...");
        sim808.send(http_cmd, sizeof(http_cmd)-1);
        while (true) {
            int ret = sim808.recv(buffer, sizeof(buffer)-1);
            if (ret <= 0){ //failure
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
