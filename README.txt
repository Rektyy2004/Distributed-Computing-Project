The code in this folder has already been compiled into .class files as 
requested by the assignment brief. The required 'keystore.jks' file is 
also included in this folder so you do not need to generate a new 
security certificate to test the SSL/TLS sockets.

# If you need to recompile the source code from scratch, use the following 
command:
    javac *.java

# HOW TO RUN THE APPLICATION
Please open your Terminal / Command Prompt and navigate to this folder.

STEP 1: START THE SERVER
You must start the server first so it can listen for secure connections.
Type the following command and press Enter:
    java SMPServer

STEP 2: START THE CLIENT(S)
Open a SECOND, completely new Terminal window, navigate to this folder, 
and choose ONE of the following client interfaces to test:

  Option A (Command Line Interface):
    java SMPClient

  Option B (Graphical User Interface - Swing):
    java SMPClientGUI

# Note: You can open multiple terminal windows and run multiple clients 
simultaneously to test the Thread Pool concurrency!