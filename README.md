# campchat

campchat is a LoRA-based communcations system that allows users to communicate using smartphones without the need for a web (either wifi or data) connection. Unlike similar systems that use bluetooth to connect to the LoRA transceiver, campchat uses dongles that take advantage of modern smartphones' USB OTG capabilities. This removes the need for a separate battery for the dongle (it is powered by the phone) and reduces the headaches involved in wireless pairing/management.  

This repository stores the android application for using the dongles, as well as the (very basic) transceiver code (under `arduino_code`). 

The android app is based on [android-chat-ui](https://github.com/timigod/android-chat-ui), a very handy library for developing messaging interfaces. Communication with the dongles over USB is provided by [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android). 

## Hardware
This project uses [ESP32 LoRA boards from LILYGO](https://www.amazon.com/LORA32-Display-Bluetooth-Development-Antenna/dp/B07C5GV243/ref=pd_sbs_147_1/135-7045927-5729408?_encoding=UTF8&pd_rd_i=B07C5GV243&pd_rd_r=cacbb8a8-fb53-4e00-b434-9cde3e34c6f5&pd_rd_w=l6mzP&pd_rd_wg=WWF0z&pf_rd_p=d9804894-61b7-40b3-ba58-197116cffd9d&pf_rd_r=5FH5C48A8EDE2QNAJ0WK&psc=1&refRID=5FH5C48A8EDE2QNAJ0WK). They support an arduino programming environment, and are set up to push what they receive over serial out through LoRA, and vice versa. They connect to your smartphone via an OTG cable.  

## Todo
- Add confirmation of receipt of message
- Add secure message functionality (encrypt/decrypt), prevents others from reading your messages
- Add group message function (pair dongles together, now multiple phones can work together)

## Notice
This system is provided, open-source, with no guarantee of its effectiveness or usefulness. While I hope it is useful if you're out camping or something, I take no responsibility for its operation in critical circumstances and it should not be relied upon as a failsafe way to communicate.
