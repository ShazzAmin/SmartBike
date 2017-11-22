 #include "DFRobot_sim808.h"

  //make sure that the baud rate of SIM900 is 9600!
  //you can use the AT Command(AT+IPR=9600) to set it through SerialDebug

  DFRobot_SIM808 sim808(&Serial);

  char http_cmd[] = "GET /test.txt HTTP/1.0\r\n\r\n";
  char buffer[512];

  void setup(){
    //mySerial.begin(9600);
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
    if(!sim808.connect(TCP, "shazz.me", 80)) {
        Serial.println("Connect error");
    }else{
        Serial.println("Connect shazz.me success");
    }

    //*********** Send a GET request *****************
    Serial.println("waiting to fetch...");
    sim808.send(http_cmd, sizeof(http_cmd)-1);
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

  }
