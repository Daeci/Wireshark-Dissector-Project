## Wireshark-Dissector-Project

# Lua dissector
DISCLAIMER: Tested only for Wireshark version 2.6.10

The plugins folder for wireshark can be located by the following steps:
1) Launch wireshark
2) Click on the "Help" tab
3) Click on "About Wireshark"
4) Navigate to the "Plugins" tab
5) You should now see the path to navigate to the epan/ directory - sometimes clicking on the path will open up a file manager/file explorer depending on OS

Now, move "capstone.lua" into the epan/ directory.
Then, relaunch Wireshark for it to be fully added to the program.

# Java UDP server & client
Need to have Java installed on your machine.
Navigate to directory in terminal.
Run commands:
1: javac UDPClient.java
2: javac UDPServer.java
to compile and create .class files

To run, open 2 terminals and navigate to the right directory:
1) command: java UDPServer
on 1st terminal to start the server

2) command: java UDPClient [filename]
on 2nd terminal where [filename] is a txt file of data to send to server, ideally in the same directory as the java files