# Chat
Gustavo Flores and Giovanni Orozco

COMP429 Programming Assignment 1
A Chat Application for Remote Message Exchange

*Member’s contributions*
Gustavo was in charge of the Server/Host part of the application and Giovanni was in charge of the Client part of the application.
Giovanni worked on the ConnectionLists.java file which was where each process would store the information of each connection whether
it was the host or the client. This file also stored the actual objects (Handler and Client) that were used to communicate with
each peer. Giovanni also worked on the Client.java file which established the connection to the host port and handled messages received from the host with appropriate actions. The Client class was extended as a thread so the user could connect to multiple hosts.
Gustavo worked on the Server.java file which created a new Handler for each connection made to the host port that ran at the same time
by extending it as a Thread. This also allowed the user to receive multiple and separate clients. Gustavo also worked on the 
Handler.java file which handled messages recieved from the client with appropriate actions.
Both Giovanni and Gustavo worked on the Chat.java file which was the main class since knowledge of the other classes were needed to
have the main java file running smoothly.

*How to install any prerequisites, build your program, and run our application.*
We made our project using java only so you can run our project by exporting the files into your desired destination. 
You can then open it using any IDE used for java files or you can use the command prompt to run it. 
To run it on the command prompt you simply open up the command prompt and enter the directy of the source files.
Once you're in the src directory or the directory with all of the .java files you can use the command "javac *.java" to compile all of 
the java files. After compiling the java files you can run the main class by using the command "java Chat" which will give 
you a random port number. However if you run it as "java Chat 4000" for example, that will run the main class with the port number of 4000.
You can run the program with any port number from 1024 to 49151

*Resources.*
1)https://www.geeksforgeeks.org/socket-programming-in-java/
2)https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
We used the first link to get an understanding of how sockets worked and how to implement a simple client and host chat in java, however 
in this example the host could only accept one client
We used the second link to show us a more effect client and host chat since this example had a host that could support multiple 
clients at a time. We used this example to help us understanding how we should implement our own chat application.

