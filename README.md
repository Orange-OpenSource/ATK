ATK  [![Build Status](https://travis-ci.org/Orange-OpenSource/ATK.png)](https://travis-ci.org/Orange-OpenSource/ATK) 
===

The Accelerator Test Kit is a software designed for testing applications on Android devices.

ATK measures device resources consumption (cpu, power, memory etc ...) in real-time, and then provides graph report (pdf and html format) as a result. It is basically made of two parts:

+ Graph Analyzer

 This tool enables you to map together different sources of data graph and events in order to perform correlation.

+ Script Recorder"

 It let you to record actions performed on the mobile device (touch screen, key press ...). These actions are translated into script language commands, and saved into test script file. Other script commands can be manually added to the script to perform special actions such as taking screenshot, make a loop on a set of actions, including another script file etc...

New: we are working on an ARO integration with ATK. This is still a work in progress but the first hooks are in place.