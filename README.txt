Ethan Honeycutt

Chat Program

The folder contains this readme and four files, server.Server.java, server.ServerProcess.java, client.Client.java, and client.ClientProcess.java.

Compile with

javac *.java

start the server with

java server.Server port
	for example,
java server.Server 8080

then start up a few clients with

java client.Client host port
	for example,
java client.Client 0.0.0.0 8080

For each of the clients, register a username by typing

REG username
	for example,
REG ethan

then you maybe use the rest of the commands:

MESG
	for messages to all users
	for example,
MESG This is a message to all users.

PMSG
	for messages to just one user
	for example,
PMSG ethan This is a message just to user ethan.

EXIT
	to disconnect from the server.