# Mirror
Currently there are three components to this project. The server and the keygen will probably be merged at some point.

## Generate Key
Compile the MirrorKeyGen file into a jar. Because I was lazy with my `.gitignore` there might already be one in the 
`out/artifacts/MirrorKeyGen_jar/` folder. 
Run this with `java -jar MirrorKeyGen.jar`. This will generate a `.ks` file and a QrCode. 

## Run Server
In the source code for MirrorServer, change the ip address to an ip you can broadcast on and a port of your choosing. Create the jar.
Move the `.ks` file into the same folder as the MirrorServer jar, and run `java -jar MirrorServer.jar`. This will ask for the filename of the keystore and a password.

## Android App
Scan QrCode that was generated from the first step. Enter ip address and port number. Press connect. You should see that it was connected
from the Server console. You can now send encrypted messages to the computer.
