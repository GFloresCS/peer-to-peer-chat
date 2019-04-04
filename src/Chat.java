import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Random;
import java.util.Scanner;

//Run by doing the following on the command line in Chat.java directory
//'javac Chat.java'
//'java Chat [arg0]' where arg0 is a specified port number
//Note: Ports up to 1023 are reserved, a specified port must be between 1024 and 49151
public class Chat
{
    private static int PORT_NUMBER;
    private static String MY_IP;
    private static boolean run = true;

    public static void main(String[] args) throws IOException
    {
        //IP Address of the user
        MY_IP = InetAddress.getLocalHost().getHostAddress();

        //If a port between 1024 and 49151 is specified, that port will open.
        // Otherwise, a random port will be assigned
        if(args.length > 0)
        {
            try
            {
                int initPort = Integer.parseInt(args[0]);

                if((1023 < initPort) && (49152 > initPort))
                {
                    PORT_NUMBER = initPort;
                }
                else
                {
                    System.out.println("The port number should be between 1024 and 49151.");
                    System.out.println("A random port number will be assigned to you, type myport to view it.");
                    Random r = new Random();
                    PORT_NUMBER = r.nextInt(49151 - 1024) + 1024;
                }
            }
            catch(NumberFormatException e)
            {
                System.err.println("Argument " + args[0] + " must be an integer.");
                System.out.println("A random port number will be assigned to you, type myport to view it.");
                Random r = new Random();
                PORT_NUMBER = r.nextInt(49151 - 1024) + 1024;
            }
        }
        else
        {
            Random r = new Random();
            PORT_NUMBER = r.nextInt(49151 - 1024) + 1024;
        }

        //An ArrayList to store the name of all connections
        ConnectionLists list = new ConnectionLists();
        //An ArrayList to store the Handler and Client objects
        ConnectionLists connections = new ConnectionLists();

        //Opens a ServerSocket that will listen on PORT_NUMBER
        ServerSocket serverS = new ServerSocket(PORT_NUMBER);
        Server mainServer = new Server(serverS, list, connections);
        mainServer.start();

        String[] tokenAns;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type 'help' for a list of commands.");

        while(run)
        {
            System.out.print(">");
            String temp = scanner.nextLine();
            tokenAns = temp.split(" ");

            switch(tokenAns[0].toLowerCase())
            {
                //Prints out all the commands
                case "help":
                    System.out.println("Commands:\n");
                    System.out.println("help: Displays a list of commands.\n");
                    System.out.println("myip: Display the IP address of this process.\n");
                    System.out.println("myport: Display the port this process is using.\n");
                    System.out.println("connect <destination ip> <port no>: Establishes a new TCP connection to the specified <destination> at the specified <port no>. The <destination> is the IP address of the computer.\n");
                    System.out.println("list: Display a numbered list of all the connections and their <connection id> that this process is part of.\n");
                    System.out.println("terminate <connection id>: This command will terminate the connection with <connection id>\n");
                    System.out.println("send <connection id> <message>: Sends a message less than 100 characters to the host/client with dedicated <connection id>.\n");
                    System.out.println("exit: Closes all connections terminates this process.\n");
                    break;

                //Print the IP Address of your machine (inside your local network)
                case "myip":
                    System.out.println("The IP address of this process: " + MY_IP);
                    break;

                //Print the port number of this process
                case "myport":
                    System.out.println("The port number of this process: " + PORT_NUMBER);
                    break;

                //Connects to host, checks if you are already connected and if you can
                // connect to the specified IP Address and port
                case "connect":
                    connect(tokenAns, connections, list);
                    break;

                //Prints out a list of clients/hosts you are currently connected to
                case "list":
                    if(connections.isEmpty())
                    {
                        System.out.println("You are not connected to anyone.");
                    }
                    else
                    {
                        list.printList();
                    }
                    break;

                //Terminates a connection with a client/host
                case "terminate":
                    int connectionID;
                    if(tokenAns.length != 2)
                    {
                        System.out.println("Error: Follow the format 'terminate <connection id>");
                    }
                    else if(isInteger(tokenAns[1]))
                    {
                        connectionID = Integer.parseInt(tokenAns[1]);
                        terminate(connectionID, connections, list);
                    }
                    else
                    {
                        System.out.println("Please enter an ID number.");
                    }
                    break;

                //Sends a message to the specified client/host
                case "send":
                    if(tokenAns.length < 3)
                    {
                        System.out.println("Error: Follow the format 'send <connection id> <message>'");
                    }
                    else
                    {
                        send(tokenAns, connections, list);
                    }
                    break;

                //Terminates all connections, closes the port/socket, and exits the program
                case "exit":
                    int i = 1;
                    int count = 1;
                    System.out.println("Closing connections with peers.");
                    while(!list.isEmpty())
                    {
                        System.out.println("Closing connection id: "+count);
                        terminate(i, connections, list);
                        count++;
                    }

                    //whenever we are not connected to anyone we have 3 threads active so wait till we have 3 or less
                    // threads to close the entire server
                    while(Thread.activeCount() > 3){}
                    if(count != 1)
                    {
                        System.out.println("All connections have been closed and peers have been notified. \nNow closing server.");
                        serverS.close();
                        //if server is closed then we'll have less than 3 threads so we can close the program.
                        while(Thread.activeCount() >= 3) {}
                    }
                    else
                    {
                        System.out.println("No peers to notify. \nNow closing server.");
                        serverS.close();
                        while(Thread.activeCount() >= 3) {}
                    }
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;

                //Any undefined command will default to this
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }

    //Connects this process to specified host
    public static void connect(String[] tokenAns, ConnectionLists connections, ConnectionLists list)
    {
        //Makes sure the format is correct
        if(tokenAns.length == 3)
        {
            //Checks to see if you are already connected to them
            if(list.contains(tokenAns[1] + " " + tokenAns[2] + " Host") || list.contains(tokenAns[1] + " " + tokenAns[2] + " Client"))
            {
                System.out.println("You are already connected to them!");
            }
            //Checks to see if you are connecting to yourself
            else if(tokenAns[1].equals(MY_IP) && tokenAns[2].equals(Integer.toString(PORT_NUMBER)))
            {
                System.out.println("You can't connect to yourself!");
            }
            //check to see if the ip is in xxx.xxx.xxx.x(x)x) format
            else if(!validIP(tokenAns[1])){
                System.out.println("That is not a valid IP address.");
            }
            //Try to establish the new connection
            else
            {
                Client newConnect = new Client(tokenAns[1], tokenAns[2], PORT_NUMBER, list);
                try
                {
                    newConnect.start();
                    connections.addList(newConnect);
                }
                catch(Exception e)
                {
                    System.out.println("Error connecting to host");
                }
            }
        }
        else
        {
            System.out.println("Error: Follow the format 'connect <destination ip> <port no>'");
        }
    }

    //Removes the thread and removes them from both the host and client lists
    public static void terminate(int connectionID, ConnectionLists connections, ConnectionLists list)
    {
        if(connectionID <= connections.size() && connectionID > 0)
        {
            int id = --connectionID;
            if(list.isClient(id))
            {
                ((Handler) connections.get(id)).endConnection();
            }
            else
            {
                ((Client) connections.get(id)).endConnection();
            }
            list.removeList(id);
        }
        else
        {
            System.out.println("Error: Please select an ID number from the list.");
        }
    }

    //Sends a message less than 100 characters to peer
    public static void send(String[] tokenAns, ConnectionLists connections, ConnectionLists list)
    {
        //Checks to make sure message is less than 100 characters
        String message;
        if(isInteger(tokenAns[1]) && Integer.parseInt(tokenAns[1]) <= connections.size() && Integer.parseInt(tokenAns[1]) > 0)
        {
            message = fuseMessage(tokenAns);
            if(message.equalsIgnoreCase("Too Long"))
            {
                System.out.println("This message is too long, make sure it is 100 characters or less.");
                System.out.println("(Including blank spaces).");
            }
            else
            {
                int id = Integer.parseInt(tokenAns[1]) - 1;
                if(list.isClient(id))
                {
                    ((Handler) connections.get(id)).sendMessage(message, id);
                }
                else
                {
                    ((Client) connections.get(id)).sendMessage(message, id);
                }
            }
        }
        else
        {
            System.out.println("Error: Please select an ID number from the list.");
        }
    }

    //Fuses the message into one string
    public static String fuseMessage(String[] message)
    {
        String temp = "";
        StringBuilder sb = new StringBuilder();
        for(int i = 2; i < message.length; i++)
        {
            sb.append(message[i]);
            sb.append(" ");
        }
        temp = sb.toString();
        if(lessThanHunned(temp))
        {
            return temp;
        }
        return "Too Long";
    }

    //Ensures the string is less than 100 characters
    public static boolean lessThanHunned(String temp)
    {
        if(temp.length() <= 100)
        {
            return true;
        }
        return false;
    }

    //Checks if the string is an integer
    public static boolean isInteger(String st)
    {
        boolean isValidInteger = false;
        try
        {
            Integer.parseInt(st);
            isValidInteger = true;
        }
        catch(NumberFormatException ex)
        {
        }
        return isValidInteger;
    }

    //checks to see if its in the format of an IPv4 address
    public static boolean validIP(String ipv) {
        try
        {
            if (ipv.endsWith(".")) {
                return false;
            }
            String[] parts = ipv.split( "\\." );
            if (parts.length != 4) {
                return false;
            }
            for (String temp : parts) {
                int i = Integer.parseInt(temp);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            return true;
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
    }
}

