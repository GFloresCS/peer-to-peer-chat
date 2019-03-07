import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Thread{
    final String ip;
    final int port;
    final int myPortNumber;
    final String ipPort;
    private ConnectionLists list;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket s;
    private boolean run = true;

    public Client(String ip, String port, int myPortNumber, ConnectionLists list){
        this.ip = ip;
        this.port = Integer.parseInt(port);
        this.myPortNumber = myPortNumber;
        this.list = list;
        ipPort = ip +" "+port;
    }

    @Override
    public void run() {
        try
        {
            String received = "";
            String[] tokenAns = null;

            // establish the connection with server port
            s = new Socket(ip, port);

            //list.addList(ip+" "+port+" Host");
            list.addList(ipPort+" Host");

            // obtaining input and out streams
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            //send the server your ip and port in case they want to connect to you
            boolean sendIP = false;

            // the following loop performs the exchange of
            // information between client and client handler
            while (run)
            {
                try {
                    received = dis.readUTF();
                    tokenAns = received.split(" ");
                } catch (IOException ex) {
                    close(true);
                }

                if(!sendIP) {
                    dos.writeUTF(InetAddress.getLocalHost().getHostAddress()+" "+myPortNumber);
                    System.out.print(received+"\n>");
                    sendIP = true;
                }
                else {
                    try {//tokenAns is sent and check for null because to avoid duplicates when terminating
                        switch (tokenAns[0]) {
                            case "send": //they sent a message so display their ip, port and message
                                if (tokenAns != null) {
                                    System.out.println();
                                    System.out.println("Message received from: " + ip);
                                    System.out.println("Sender's Port: " + port);
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

                            /*case ">":
                                System.out.print(">");
                                break;*/
                        }
                    } catch(NullPointerException e) {

                    }
                }
            }
        }catch(Exception e){
            //e.printStackTrace();
            //couldn't connect to the socket, check if the two lists are same size, if it is
            //then remove last from each list if not remove last from connections since it adds to connections first
            if(list.synced()) {
                list.removeList(list.size()-1);
            }
            else {
                list.removeLastCon();
            }
            System.out.print("Could not connect to that specific ip and port number.\n>");
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
            int id = list.getID(ipPort+" Host");
            if(id > -1) {
                list.removeList(id);
            }

            if (iClosedIt) {
                System.out.print("A connection has been closed with ip and port number: ["+ip+" ] ["+port+"]\n>");
            }
            else {
                System.out.print("The other peer with ip and port number: ["+ip+" ] ["+port+
                        "] has terminated your connection.\n>");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
