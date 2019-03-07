import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler extends Thread{
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    private ConnectionLists list;
    private String theirIPPort;
    private String theirPort;
    private String theirIP;
    private boolean run = true;

    public Handler(Socket s, DataInputStream dis, DataOutputStream dos, ConnectionLists list) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.list = list;
    }

    @Override
    public void run()  {
        String received = "";
        String[] tokenAns = null;

        //Display a message to show the client successful connection has been made
        try {
            dos.writeUTF("Successfully connected!");
            dos.writeUTF(">");
        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
        //have client send ip so we can display in our list of connections
        boolean receivedIP = false;

        while (run)
        {
            try {
                // receive the answer from client
                try {
                    received = dis.readUTF();
                    tokenAns = received.split(" ");
                } catch (IOException ex) {
                    close(true);
                }

                //only receive IP and port number once, it'll be the first thing they automatically send
                if(!receivedIP) {
                    theirIP = tokenAns[0];
                    theirPort = tokenAns[1];
                    System.out.println("Their ip is "+theirIP+
                            " and their port no. is "+theirPort+"\n");
                    System.out.print(">");
                    theirIPPort = theirIP +" "+ theirPort;
                    list.addList(theirIPPort+" Client");
                    receivedIP = true;
                }
                else {
                    try {
                        switch(tokenAns[0]) {
                            case "send": //they sent a message so display their ip, port and message
                                if(tokenAns != null) {
                                    String[] temp = theirIPPort.split(" ");
                                    System.out.println("Message received from: " + temp[0]);
                                    System.out.println("Sender's Port: " + temp[1]);
                                    System.out.print("Message: ");
                                    for (int i = 1; i < tokenAns.length; i++) {
                                        System.out.print(tokenAns[i] + " ");
                                    }
                                    System.out.println();
                                    System.out.print(">");
                                    tokenAns = null;
                                }
                                break;

                            case "terminate":
                                close(false);
                                break;
                        }
                    } catch (NullPointerException e) {

                    }
                }
            } catch (Exception e) { //couldnt connect to the socket, check if the two lists are same size, if it is
                //then remove last from each list if not remove last from connections since it adds to connections first
                //e.printStackTrace();
                if(list.synced()) {
                    list.removeList(list.size()-1);
                }
                else {
                    list.removeLastCon();
                }
            }
        }
    }

    //sends a message to a peer and prints to the user which id number of the peer
    public void sendMessage(String message, int id) {
        try {
            dos.writeUTF("send "+message);
            System.out.println("Message sent to: ID# "+ ++id);
        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //sends a message to the specific peer to tell them to close their connections with us
    public void endConnection() {
        try {
            dos.writeUTF("terminate");
            run = false;
        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //a peer wants to terminate connection so close inputstream, outputstream, as well as the socket
    //remove them from your list of connections and then display a message to the user
    public void close(boolean iClosedIt) {
        try {
            run = false;
            this.dis.close();
            this.dos.close();
            s.close();

            //if its -1 then it's not in the list and has been removed already
            int id = list.getID(theirIPPort+" Client");
            if(id > -1) {
                list.removeList(id);
            }

            if (iClosedIt) {
                System.out.print("A connection has been closed with ip and port number: ["+theirIP+"] ["+theirPort+"]\n>");
            }
            else {
                System.out.print("The other peer with ip and port number: ["+theirIP+"] ["+theirPort+
                        "] has terminated your connection.\n>");
            }
        } catch (Exception e){
            //e.printStackTrace();
        }
    }
}
