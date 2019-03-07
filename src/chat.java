import java.io.*;
import java.util.*;
import java.net.*;

//ran by doing the following on the command line in chat.java directory
//"javac chat.java"
//"java chat arg0" where arg0 is the specified port number that they want to use
public class chat {

    //public static ArrayList<String> iPAddress = new ArrayList<String>();
    private static int PORT_NUMBER;
    private static String MY_IP;
    private static boolean run = true;

    public static void main(String[] args) throws IOException {
        //save the ip of this user
        MY_IP = InetAddress.getLocalHost().getHostAddress();

        //make sure they input a port number from 1024-49151 if they didn't send the port number in the
        // command line then pick a random port from 5000-6000 or if they made an error just assign a random port
        int initPort;
        if (args.length > 0) {
            try {
                initPort = Integer.parseInt(args[0]);
                if((1023 < initPort) && (49152 > initPort)) {
                    PORT_NUMBER = initPort;
                }
                else {
                    System.out.println("The port number should be between 1024 and 49151.");
                    System.out.println("A random port number will be assigned to you, type myport to view it.");
                    Random r = new Random();
                    PORT_NUMBER = r.nextInt(49151 - 1024) + 1024;
                }
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.out.println("A random port number will be assigned to you, type myport to view it.");
                Random r = new Random();
                PORT_NUMBER = r.nextInt(49151 - 1024) + 5000;
            }
        }
        else {
            Random r = new Random();
            PORT_NUMBER = r.nextInt(49151 - 1024) + 1024;
        }

        //create an arraylist to store the names of all connections and another to store the actual objects handlers,clients
        ConnectionLists list = new ConnectionLists ();
        ConnectionLists connections = new ConnectionLists ();

        // server is listening on port 5000-6000
        ServerSocket serverS = new ServerSocket(PORT_NUMBER);
        Server mainServer = new Server(serverS, list, connections);
        mainServer.start();

        String[] tokenAns;
        Scanner scanner = new Scanner(System.in);

        while (run) {
            System.out.print(">");
            String temp = scanner.nextLine();
            tokenAns = temp.split(" ");

            switch (tokenAns[0].toLowerCase()) {
                //print out all the commands and what they each do
                case "help":
                    System.out.println("The different commands are: ");
                    System.out.println("help: Display information about "
                            + "the available user interface options or command manual.\n");
                    System.out.println("myip: Display the IP address of this process.\n");
                    System.out.println("myport: Display the port on which "
                            + "this process is listening for incoming connections.\n");
                    System.out.println("connect: <destination ip>  <port  no>  :  "
                            + "This command establishes a new TCP connection to the "
                            + "specified<destination> at the specified < port no>. "
                            + "The <destination> is the IP address of the computer.\n");
                    System.out.println("list: Display a numbered list of all "
                            + "the connections this process is part of.\n");
                    System.out.println("terminate:  <connection  id.>  This  "
                            + "command  will  terminate  the  connection  "
                            + "listed  under  the  specifiednumber  when  "
                            + "LIST  is  used  to  display  all  connections. \n");
                    System.out.println("send: <connection id.> <message> "
                            + "(For example, send 3 Oh! This project is a "
                            + "piece of cake). This willsend the message to "
                            + "the host on the connection that is designated "
                            + "by the number 3 when command list is used. \n");
                    System.out.println("exit: Close all connections and "
                            + "terminate this process.\n");
                    break;

                //first check if we're already connected by checking the list they might be client or host so check both
                case "connect":
                    connect(tokenAns, connections, list);
                    break;

                // print the IP Address of your machine (inside your local network)
                case "myip":
                    System.out.println("The IP address of this process: " +
                            MY_IP);
                    break;

                case "myport":
                    System.out.println("The port number of this process: " +
                            PORT_NUMBER);
                    break;

                case "list":
                    if (connections.isEmpty()) {
                        System.out.println("You are not connected to anyone.");
                    } else {
                        list.printList();
                    }
                    break;

                //Check to make sure id number is valid
                case "terminate":
                    int idInt;
                    if(isInteger(tokenAns[1])) {
                        idInt = Integer.parseInt(tokenAns[1]);
                        terminate(idInt, connections, list);
                    }
                    else {
                        System.out.println("Please enter an ID number.");
                    }
                    break;

                //Check to make sure id number is valid
                case "send":
                    send(tokenAns, connections, list);
                    break;

                //if they write exit then close all connections, the server and then terminate this process
                //if list is empty then you can just close the server otherwise close every connection first then the server
                case "exit":
                    int i=1;
                    int count=0;
                    while(!list.isEmpty()) {
                        terminate(i, connections, list);
                        count++;
                    }
                    serverS.close();
                    if(count != 0) {
                        System.out.print("Connections closed. \nPeers have been notified. \nServer Closed. \nGoodbye!");
                    }
                    else {
                        System.out.print("No connections to close. \nServer Closed. \nGoodbye!");
                    }
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid input\n");
                    break;
            }
        }
    }

    //make sure they sent 3 things, make sure they arent already connected to you, make sure youre not connecting to yourself
    public static void connect(String[] tokenAns, ConnectionLists connections, ConnectionLists list) {
        if(tokenAns.length == 3) {
            if(list.contains(tokenAns[1]+" "+tokenAns[2]+" Host")
                    || list.contains(tokenAns[1]+" "+tokenAns[2]+" Client") ) {
                System.out.println("You are already connected to them!");
            }
            else if(tokenAns[1].equals(MY_IP) && tokenAns[2].equals(Integer.toString(PORT_NUMBER))) {
                System.out.println("You can't connect to yourself!");
            }
            else {
                Client newConnect = new Client(tokenAns[1], tokenAns[2], PORT_NUMBER, list);
                try {
                    newConnect.start();
                    connections.addList(newConnect);
                }
                catch(Exception e) {
                }
            }
        }
        else {
            System.out.println("You must input: connect <destination ip>  <port  no>");
        }
    }

    //remove the thread and send them a message to remove me from their list
    //remove from the list of connections which will remove from both lists
    public static void terminate(int sid, ConnectionLists connections, ConnectionLists list) {
        if(sid <= connections.size() && sid > 0) {
            int id = --sid;
            if(list.isClient(id)) {
                ((Handler)connections.get(id)).endConnection();
            }
            else {
                ((Client)connections.get(id)).endConnection();
            }
            list.removeList(id);
        }
        else {
            System.out.println("Error: Please select an ID number from the list.");
        }
    }

    //check if its the connected peer is host or client, since we're storing 2 objects in the array
    public static void send(String[] tokenAns, ConnectionLists connections, ConnectionLists list) {
        String message;
        if(isInteger(tokenAns[1]) && Integer.parseInt(tokenAns[1]) <= connections.size() && Integer.parseInt(tokenAns[1]) > 0){
            message = fuseMessage(tokenAns);
            if(message.equalsIgnoreCase("Too Long")) {
                System.out.println("This message is too long, make sure it is 100 characters or less .");
                System.out.println("(Including blank spaces.)");
            }
            else {
                int id = Integer.parseInt(tokenAns[1])-1;
                if(list.isClient(id)) {
                    ((Handler)connections.get(id)).sendMessage(message, id);
                }
                else {
                    ((Client)connections.get(id)).sendMessage(message, id);
                }
            }
        }
        else {
            System.out.println("Error: Please select an ID number from the list.");
        }
    }

    //make the message into one string and make sure its less than 100 char
    public static String fuseMessage(String[] message) {
        String temp="";
        StringBuilder sb = new StringBuilder();
        for(int i = 2; i<message.length; i++) {
            sb.append(message[i]);
            sb.append(" ");
        }
        temp = sb.toString();
        if(lessThanHunned(temp)) {
            return temp;
        }
        return "Too Long";
    }

    //makes sure a string is 100 or less characters
    public static boolean lessThanHunned(String temp) {
        if(temp.length() <= 100){
            return true;
        }
        return false;
    }

    //check if a string they sent is actually an integer
    public static boolean isInteger(String st) {
        boolean isValidInteger = false;
        try{
            Integer.parseInt(st);
            isValidInteger = true;
        }catch (NumberFormatException ex){}
        return isValidInteger;
    }
}

