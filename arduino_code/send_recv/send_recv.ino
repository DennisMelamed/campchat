// SPI LoRa Radio
#define LORA_SCK 5        // GPIO5 - SX1276 SCK
#define LORA_MISO 19     // GPIO19 - SX1276 MISO
#define LORA_MOSI 27    // GPIO27 - SX1276 MOSI
#define LORA_CS 18     // GPIO18 - SX1276 CS
#define LORA_RST 14   // GPIO14 - SX1276 RST
#define LORA_IRQ 26  // GPIO26 - SX1276 IRQ (interrupt request)
#include <SPI.h>
#include <LoRa.h>       // https://github.com/sandeepmistry/arduino-LoRa

String rssi = "";
String packet = "";

void setup() {
  Serial.begin(115200);
  while (!Serial);

  Serial.println("LoRa Receiver");

 

  // Very important for SPI pin configuration!
  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_CS); 
  
  // Very important for LoRa Radio pin configuration! 
  LoRa.setPins(LORA_CS, LORA_RST, LORA_IRQ);         

  //pinMode(blueLED, OUTPUT); // For LED feedback
  
  if (!LoRa.begin(915E6)) {
    Serial.println("Starting LoRa failed!");
    while (1);
  }
  
  // The larger the spreading factor the greater the range but slower data rate
  // Send and receive radios need to be set the same
  LoRa.setSpreadingFactor(12);  // ranges from 6-12, default 7 see API docs
}

void loop() {
  // try to parse packet coming in from lora
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    packet = "";                   // Clear packet
    while (LoRa.available()) {
      packet += (char)LoRa.read(); // Assemble new packet
    }
    rssi = LoRa.packetRssi();



    //digitalWrite(blueLED, OFF); // Turn blue LED off
    
    Serial.println(packet);     
  }
  //try to send a packet
  if (Serial.available() > 0) {
    String incomingByte = Serial.readString();

    LoRa.beginPacket();

    LoRa.print(incomingByte);
    LoRa.endPacket();
  }

}
