# campchat

campchat is a LoRA-based communcations system that allows users to communicate using smartphones without the need for a web (either wifi or data) connection. Unlike similar systems that use bluetooth to connect to the LoRA transceiver, campchat uses dongles that take advantage of modern smartphones' USB OTG capabilities. This removes the need for a separate battery for the dongle (it is powered by the phone) and reduces the headaches involved in wireless pairing/management.  

This repository stores the android application for using the dongles, as well as the (very basic) transceiver code (under `arduino_code`). 

The android app is based on [android-chat-ui](https://github.com/timigod/android-chat-ui), a very handy library for developing messaging interfaces. 

## Hardware
This project uses ESP32 LoRA boards from LILYGO. They support an arduino programming environment, and are set up to push what they receive over serial out through LoRA, and vice versa. They connect to your smartphone via an OTG cable.  

## Notice
This system is provided, open-source, with no guarantee of its effectiveness or usefulness. While I hope it is useful if you're out camping or something, I take no responsibility for its operation in critical circumstances and it should not be relied upon as a failsafe way to communicate.
