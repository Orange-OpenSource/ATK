package com.orange.atk.monitor;


interface IATKMonitorEventListener  {
 void globalChanged(in String global,in String totalmem);
}