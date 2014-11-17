#!/bin/sh
# simple helper script to launch ATK with GUI or not
# use the -gui command line parameter to launch the
# GUI, otherwise stay with the console version

classpath="libs/*:build/libs/*"

cmdline=("$@")
gui=0
i=0

for p in "${cmdline[@]}"; do
    if [ $p != "-gui" ]
    then
       param[i]=$p
       i=$(($i+1))
    else
       gui=1
    fi
done

if [ "$gui" -eq "1" ]
then
    java -cp ${classpath} com.orange.atk.launcher.LaunchGUIJATK
else
    java -cp  ${classpath} com.orange.atk.launcher.LaunchJATK ${param[@]}
fi